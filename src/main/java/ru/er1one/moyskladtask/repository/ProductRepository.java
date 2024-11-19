package ru.er1one.moyskladtask.repository;

import org.springframework.stereotype.Repository;
import ru.er1one.moyskladtask.exception.ProductNotFoundException;
import ru.er1one.moyskladtask.model.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ProductRepository {

    private final AtomicLong lastId = new AtomicLong(0);
    private final Map<Long, Product> products;

    public ProductRepository() {
        products = new HashMap<>();
    }

    public List<Product> findAll() {
        return products.values().stream().toList();
    }

    public Product findById(long id) throws ProductNotFoundException {
        if (!products.containsKey(id)) {
            throw new ProductNotFoundException();
        }
        return products.get(id);
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setId(lastId.incrementAndGet());
        }
        products.put(product.getId(), product);
        return product;
    }

    public void deleteById(long id) throws ProductNotFoundException {
        if (!products.containsKey(id)) {
            throw new ProductNotFoundException();
        }
        products.remove(id);
    }



}
