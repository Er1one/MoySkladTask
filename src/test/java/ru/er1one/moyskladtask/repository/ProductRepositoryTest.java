package ru.er1one.moyskladtask.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.er1one.moyskladtask.Application;
import ru.er1one.moyskladtask.model.Product;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ExtendWith(SpringExtension.class)
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    private Product testProduct;

    @BeforeEach
    public void setup() {
        productRepository.deleteAll();

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
    public void testFindById() {
        Product savedProduct = productRepository.save(testProduct);
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals(savedProduct.getName(), foundProduct.get().getName());
    }

    @Test
    public void testFindByIdNotFound() {
        Optional<Product> foundProduct = productRepository.findById(999L);
        assertTrue(foundProduct.isEmpty());
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
    public void testDeleteById() {
        Product savedProduct = productRepository.save(testProduct);
        productRepository.deleteById(savedProduct.getId());
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());
        assertTrue(deletedProduct.isEmpty());
    }

    @Test
    public void testDeleteByIdNotFound() {
        assertDoesNotThrow(() -> productRepository.deleteById(999L));
    }
}
