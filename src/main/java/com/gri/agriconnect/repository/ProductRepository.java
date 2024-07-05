package com.gri.agriconnect.repository;



import com.gri.agriconnect.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product, String> {
}
