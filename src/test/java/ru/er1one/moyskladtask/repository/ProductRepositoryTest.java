package ru.er1one.moyskladtask.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.er1one.moyskladtask.exception.ProductNotFoundException;
import ru.er1one.moyskladtask.model.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductRepositoryTest {

    private ProductRepository productRepository;
    private Product testProduct;

    @BeforeEach
    public void setup() {
        productRepository = new ProductRepository();
        testProduct = new Product();
        testProduct.setName("Тестовый товар");
        testProduct.setPrice(100.0);
        testProduct.setDescription("Тестовое описание");
        testProduct.setInStock(false);
    }

    @Test
    public void testFindAll() {
        productRepository.save(testProduct);
        List<Product> products = productRepository.findAll();
        assertEquals(1, products.size());
        assertEquals(testProduct.getName(), products.getFirst().getName());
    }

    @Test
    public void testFindById() throws ProductNotFoundException {
        Product savedProduct = productRepository.save(testProduct);
        Product foundProduct = productRepository.findById(savedProduct.getId());
        assertNotNull(foundProduct);
        assertEquals(savedProduct.getName(), foundProduct.getName());
    }

    @Test
    public void testFindByIdNotFound() {
        assertThrows(ProductNotFoundException.class, () -> productRepository.findById(999L));
    }

    @Test
    public void testSave() {
        Product savedProduct = productRepository.save(testProduct);
        assertNotNull(savedProduct.getId());
        assertEquals(testProduct.getName(), savedProduct.getName());
    }

    @Test
    public void testSaveWithoutId() {
        Product newProduct = new Product();
        newProduct.setName("Новый товар");
        newProduct.setPrice(200.0);
        newProduct.setDescription("Новое описание");
        newProduct.setInStock(true);

        Product savedProduct = productRepository.save(newProduct);
        assertNotNull(savedProduct.getId());
        assertEquals("Новый товар", savedProduct.getName());
    }

    @Test
    public void testDeleteById() throws ProductNotFoundException {
        Product savedProduct = productRepository.save(testProduct);
        productRepository.deleteById(savedProduct.getId());
        assertThrows(ProductNotFoundException.class, () -> productRepository.findById(savedProduct.getId()));
    }

    @Test
    public void testDeleteByIdNotFound() {
        assertThrows(ProductNotFoundException.class, () -> productRepository.deleteById(999L));
    }
}
