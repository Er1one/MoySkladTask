package ru.er1one.moyskladtask.controller;

import jakarta.validation.Valid;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.er1one.moyskladtask.exception.ProductNotFoundException;
import ru.er1one.moyskladtask.exception.ProductValidationException;
import ru.er1one.moyskladtask.model.Product;
import ru.er1one.moyskladtask.service.ProductService;

import java.util.List;
import java.util.Map;

@Setter
@RestController
@RequestMapping("/api/product")
public class ProductController {

    private ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable(name = "id") long id) throws ProductNotFoundException {
        Product product = productService.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> addProduct(@Valid @RequestBody Product product, BindingResult result) throws ProductValidationException {
        if (result.hasErrors()) {
            var message = result.getFieldError() == null ? "Ошибка валидации" : result.getFieldError().getDefaultMessage();
            throw new ProductValidationException(message);
        }
        Product createdProduct = productService.addProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(@PathVariable(name = "id") long id, @RequestBody Map<String, Object> updates) throws ProductNotFoundException, ProductValidationException {
        Product updatedProduct = productService.updateProduct(id, updates);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> putProduct(@PathVariable(name = "id") long id, @RequestBody Product product) throws ProductNotFoundException, ProductValidationException {
        productService.updateProduct(id, product);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable(name = "id") long id) throws ProductNotFoundException {
        productService.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
