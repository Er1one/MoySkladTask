package ru.er1one.moyskladtask.exception;

import jakarta.validation.ConstraintViolation;
import lombok.experimental.StandardException;
import ru.er1one.moyskladtask.model.Product;

import java.util.Set;

@StandardException
public class ProductValidationException extends RuntimeException {
    public ProductValidationException(Set<ConstraintViolation<Product>> violations) {
        super(String.join(", ", violations.stream().map(ConstraintViolation::getMessage).toList()));
    }
}
