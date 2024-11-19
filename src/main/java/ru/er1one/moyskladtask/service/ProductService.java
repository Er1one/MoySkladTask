package ru.er1one.moyskladtask.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.er1one.moyskladtask.exception.ProductNotFoundException;
import ru.er1one.moyskladtask.exception.ProductValidationException;
import ru.er1one.moyskladtask.model.Product;
import ru.er1one.moyskladtask.repository.ProductRepository;
import ru.er1one.moyskladtask.util.ProductValidator;

import java.util.List;
import java.util.Map;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(long id) throws ProductNotFoundException {
        return productRepository.findById(id);
    }

    public Product addProduct(Product product) throws ProductValidationException {
        ProductValidator.validate(product);
        return productRepository.save(product);
    }

    public Product updateProduct(long id, Product product) throws ProductNotFoundException, ProductValidationException {
        Product existingProduct = productRepository.findById(id);
        ProductValidator.validate(product);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setInStock(product.getInStock());
        return productRepository.save(existingProduct);
    }

    public Product updateProduct(long id, Map<String, Object> updates) throws ProductNotFoundException, ProductValidationException {
        Product existingProduct = productRepository.findById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    existingProduct.setName((String) value);
                    break;
                case "description":
                    existingProduct.setDescription((String) value);
                    break;
                case "price":
                    existingProduct.setPrice((Double) value);
                    break;
                case "inStock":
                    existingProduct.setInStock((Boolean) value);
                    break;
                default:
                    throw new ProductValidationException("Неверное поле: " + key);
            }
        });

        ProductValidator.validate(existingProduct);
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(long id) throws ProductNotFoundException {
        productRepository.deleteById(id);
    }
}

