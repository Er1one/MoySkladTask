package ru.er1one.moyskladtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.er1one.moyskladtask.exception.ProductNotFoundException;
import ru.er1one.moyskladtask.handler.GlobalExceptionHandler;
import ru.er1one.moyskladtask.model.Product;
import ru.er1one.moyskladtask.service.ProductService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = { ProductController.class, GlobalExceptionHandler.class })
public class ProductControllerTest {

    @MockBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    public void setup() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Тестовый товар");
        testProduct.setPrice(100.0);
        testProduct.setDescription("Тестовое описание");
        testProduct.setInStock(false);
    }

    @Test
    public void testFindAll() throws Exception {
        when(productService.getAllProducts(any(), any(), any(), any(), any())).thenReturn(List.of(testProduct));

        mockMvc.perform(get("/api/product").param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Тестовый товар"))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[0].description").value("Тестовое описание"))
                .andExpect(jsonPath("$[0].inStock").value(false));
    }

    @Test
    public void testFindAllWithFilters() throws Exception {
        when(productService.getAllProducts(eq("Тест"), eq(50.0), eq(150.0), eq(true), any())).thenReturn(List.of(testProduct));

        mockMvc.perform(get("/api/product")
                        .param("name", "Тест")
                        .param("priceMin", "50.0")
                        .param("priceMax", "150.0")
                        .param("inStock", "true")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Тестовый товар"))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[0].description").value("Тестовое описание"))
                .andExpect(jsonPath("$[0].inStock").value(false));
    }

    @Test
    public void testFindAllWithSorting() throws Exception {
        when(productService.getAllProducts(any(), any(), any(), any(), any())).thenReturn(List.of(testProduct));

        mockMvc.perform(get("/api/product")
                        .param("sortField", "price")
                        .param("sortOrder", "desc")
                        .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Тестовый товар"))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[0].description").value("Тестовое описание"))
                .andExpect(jsonPath("$[0].inStock").value(false));
    }

    @Test
    public void testFindById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(testProduct);

        mockMvc.perform(get("/api/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Тестовый товар"));
    }

    @Test
    public void testFindByIdNotFound() throws Exception {
        when(productService.getProductById(999L)).thenThrow(new ProductNotFoundException("Товар не найден"));

        mockMvc.perform(get("/api/product/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Товар не найден"));
    }

    @Test
    public void testCreateProduct() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("Новый товар");
        newProduct.setPrice(200.0);
        newProduct.setDescription("Новое описание");
        newProduct.setInStock(true);

        when(productService.addProduct(any(Product.class))).thenReturn(newProduct);

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Новый товар"))
                .andExpect(jsonPath("$.price").value(200.0));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        long productId = 1L;
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Обновленное название");
        updatedProduct.setDescription("Обновленное описание");
        updatedProduct.setPrice(200.0);
        updatedProduct.setInStock(true);

        when(productService.updateProduct(eq(productId), any(Product.class))).thenReturn(updatedProduct);

        mockMvc.perform(put("/api/product/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/product/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testCreateProductWithInvalidData() throws Exception {
        Product invalidProduct = new Product();

        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidProduct)))
                .andExpect(status().isBadRequest());
    }
}
