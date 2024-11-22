package ru.er1one.moyskladtask.service;

import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import ru.er1one.moyskladtask.exception.ProductNotFoundException;
import ru.er1one.moyskladtask.exception.ProductValidationException;
import ru.er1one.moyskladtask.model.Product;
import ru.er1one.moyskladtask.repository.ProductRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Mock
    private Validator validator;

    private Product testProduct;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Тестовый товар");
        testProduct.setPrice(100.0);
        testProduct.setDescription("Тестовое описание");
        testProduct.setInStock(false);
        when(validator.validate(any(Product.class))).thenReturn(Collections.emptySet());
    }

    @Test
    public void testGetAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(testProduct)));

        List<Product> products = productService.getAllProducts(null, null, null, null, pageable);
        assertEquals(1, products.size());
        assertEquals(testProduct.getName(), products.getFirst().getName());
    }

    @Test
    public void testGetAllProductsWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(testProduct)));

        List<Product> products = productService.getAllProducts("Тестовый", 50.0, 150.0, true, pageable);
        assertEquals(1, products.size());
        assertEquals(testProduct.getName(), products.getFirst().getName());
    }

    @Test
    public void testGetAllProductsWithSorting() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("price").ascending());
        when(productRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(testProduct)));

        List<Product> products = productService.getAllProducts(null, null, null, null, pageable);
        assertEquals(1, products.size());
        assertEquals(testProduct.getName(), products.getFirst().getName());
    }


    @Test
    public void testGetProductById() throws ProductNotFoundException {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        Product foundProduct = productService.getProductById(1L);
        assertNotNull(foundProduct);
        assertEquals("Тестовый товар", foundProduct.getName());
    }

    @Test
    public void testGetProductByIdNotFound() {
        when(productRepository.findById(anyLong())).thenThrow(new ProductNotFoundException());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    public void testAddProduct() throws ProductValidationException {
        Product newProduct = new Product();
        newProduct.setName("Новый товар");
        newProduct.setPrice(200.0);
        newProduct.setDescription("Новое описание");
        newProduct.setInStock(true);

        when(productRepository.save(any(Product.class))).thenReturn(newProduct);

        Product savedProduct = productService.addProduct(newProduct);

        assertNotNull(savedProduct, "Продукт должен быть сохранен и не быть null");
        assertEquals("Новый товар", savedProduct.getName());
        assertEquals(200.0, savedProduct.getPrice());
        assertEquals("Новое описание", savedProduct.getDescription());
        assertTrue(savedProduct.getInStock());
    }

    @Test
    public void testAddProductWithInvalidData() throws ProductValidationException {
        Product invalidProduct = new Product();
        invalidProduct.setName("");

        assertThrows(ProductValidationException.class, () -> productService.addProduct(invalidProduct));
    }

    @Test
    public void testUpdateProduct() throws ProductNotFoundException, ProductValidationException {
        Product updatedProduct = new Product();
        updatedProduct.setId(1L);
        updatedProduct.setName("Обновленный товар");
        updatedProduct.setPrice(250.0);
        updatedProduct.setDescription("Обновленное описание");
        updatedProduct.setInStock(true);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        Product result = productService.updateProduct(1L, updatedProduct);

        assertNotNull(result);
        assertEquals("Обновленный товар", result.getName());
        assertEquals(250.0, result.getPrice());
    }

    @Test
    public void testUpdateProductNotFound() {
        when(productRepository.findById(anyLong())).thenThrow(new ProductNotFoundException());

        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(999L, testProduct));
    }

    @Test
    public void testUpdateProductWithInvalidData() throws ProductNotFoundException {
        Product invalidProduct = new Product();

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        assertThrows(ProductValidationException.class, () -> productService.updateProduct(1L, invalidProduct));
    }

    @Test
    public void testDeleteProduct() throws ProductNotFoundException {
        doNothing().when(productRepository).deleteById(anyLong());

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteProductNotFound() {
        doThrow(new ProductNotFoundException()).when(productRepository).deleteById(anyLong());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(999L));
    }
}
