package ru.er1one.moyskladtask.util;

import lombok.experimental.UtilityClass;
import ru.er1one.moyskladtask.exception.ProductValidationException;
import ru.er1one.moyskladtask.model.Product;

@UtilityClass
public class ProductValidator {

    public void validate(Product product) throws ProductValidationException {
        validateName(product.getName());
        validateDescription(product.getDescription());
        validatePrice(product.getPrice());
    }

    private void validateName(String name) throws ProductValidationException {
        if (name == null || name.isEmpty() || name.trim().isEmpty()) {
            throw new ProductValidationException("Название товара не может быть пустым");
        }
        else if (name.length() > 255) {
            throw new ProductValidationException("Название товара не может превышать 255 символов");
        }
    }

    private static void validateDescription(String description) throws ProductValidationException {
        if (description != null && description.length() > 4096) {
            throw new ProductValidationException("Описание товара не может превышать 4096 символов");
        }
    }

    private static void validatePrice(Double price) throws ProductValidationException {
        if (price == null) {
            throw new ProductValidationException("Цена товара не может быть пустой");
        }
        if (price < 0) {
            throw new ProductValidationException("Цена товара не может быть отрицательной");
        }
    }

}
