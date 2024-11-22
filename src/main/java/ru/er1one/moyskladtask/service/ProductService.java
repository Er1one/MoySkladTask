package ru.er1one.moyskladtask.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.er1one.moyskladtask.exception.ProductNotFoundException;
import ru.er1one.moyskladtask.exception.ProductValidationException;
import ru.er1one.moyskladtask.model.Product;
import ru.er1one.moyskladtask.repository.ProductRepository;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Validated
public class ProductService {

    private final ProductRepository productRepository;
    private final Validator validator;

    @Autowired
    public ProductService(ProductRepository productRepository, Validator validator) {
        this.productRepository = productRepository;
        this.validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    public List<Product> getAllProducts(String name, Double priceMin, Double priceMax, Boolean inStock, Pageable pageable) {
        Specification<Product> spec = Specification.where(null);

        // Фильтрация по имени (Если запрос содержит % name %)
        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get("name"), "%" + name + "%"));
        }

        // Фильтрация по минимальной цене
        if (priceMin != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"), priceMin));
        }

        // Фильтрация по максимальной цене
        if (priceMax != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"), priceMax));
        }

        // Фильтрация по наличию
        if (inStock != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("inStock"), inStock));
        }

        return productRepository.findAll(spec, pageable).getContent();
    }


    public Product getProductById(long id) throws ProductNotFoundException {
        return productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
    }

    public Product addProduct(Product product) throws ProductValidationException {
        product.setId(null);
        validateProduct(product);
        return productRepository.save(product);
    }

    public Product updateProduct(long id, Product product) throws ProductNotFoundException, ProductValidationException {
        Product existingProduct = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setInStock(product.getInStock());
        validateProduct(existingProduct);
        return productRepository.save(existingProduct);
    }

    public Product updateProduct(long id, Map<String, Object> updates) throws ProductNotFoundException, ProductValidationException {
        Product existingProduct = productRepository.findById(id).orElseThrow(ProductNotFoundException::new);

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

        validateProduct(existingProduct);
        return productRepository.save(existingProduct);
    }

    public void deleteProduct(long id) throws ProductNotFoundException {
        productRepository.deleteById(id);
    }

    private void validateProduct(Product product) throws ProductValidationException {
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        if (!violations.isEmpty()) {
            throw new ProductValidationException(violations);
        }
    }
}
