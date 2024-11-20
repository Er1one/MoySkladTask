package ru.er1one.moyskladtask.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.er1one.moyskladtask.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
