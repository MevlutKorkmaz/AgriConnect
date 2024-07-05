package com.gri.agriconnect.repository;



import com.gri.agriconnect.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategory(String category);
    List<Product> findBySupplierId(String supplierId);
}
