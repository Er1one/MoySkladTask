package ru.er1one.moyskladtask.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Data
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 255, message = "Название продукта не может превышать 255 символов")
    @NotBlank(message = "Название продукта не может быть пустым")
    @NotNull(message = "Название продукта не может быть пустым")
    private String name;

    @Size(max = 4096, message = "Описание продукта не может превышать 4096 символов")
    @ColumnDefault("''")
    private String description;

    @Min(value = 0, message = "Цена не может быть отрицательной")
    @ColumnDefault("0.0")
    private Double price;

    @ColumnDefault("false")
    private Boolean inStock;

    public Product() {
        this.inStock = false;
        this.price = 0.0;
        this.description = "";
    }

}
