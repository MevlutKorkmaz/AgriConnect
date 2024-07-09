package com.gri.agriconnect.repository;

import com.gri.agriconnect.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findBySupplierId(String supplierId);
    List<Product> findByCategoryTagsContaining(String tag); // Find products by category tags
    List<Product> findByNameContainingOrDescriptionContaining(String name, String description); // Find products by name or description
}
