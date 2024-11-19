package ru.er1one.moyskladtask.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Product {

    private Long id;

    @NotBlank(message = "Название продукта не может быть пустым")
    private String name;

    private String description = "";

    @Positive(message = "Цена продукта должна быть положительным числом")
    private Double price;

    private Boolean inStock = false;

}
