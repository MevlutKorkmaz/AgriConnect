package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Product;
import com.gri.agriconnect.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product saveProduct(Product product) {
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public List<Product> getProductsBySupplierId(String supplierId) {
        return productRepository.findBySupplierId(supplierId);
    }

    public Optional<Product> getProductById(String productId) {
        return productRepository.findById(productId);
    }

    public void deleteProduct(String productId) {
        productRepository.deleteById(productId);
    }

    public Product updateProduct(String productId, Product updatedProduct) {
        return productRepository.findById(productId).map(product -> {
            product.setName(updatedProduct.getName());
            product.setCategory(updatedProduct.getCategory());
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            product.setSupplierId(updatedProduct.getSupplierId());
            product.setStockQuantity(updatedProduct.getStockQuantity());
            product.setFavoriteCount(updatedProduct.getFavoriteCount());
            product.setLikeCount(updatedProduct.getLikeCount());
            product.setCommentCount(updatedProduct.getCommentCount());
            product.setCommentIds(updatedProduct.getCommentIds());
            product.setUpdatedAt(LocalDateTime.now());
            return productRepository.save(product);
        }).orElseGet(() -> {
            updatedProduct.setProductId(productId);
            updatedProduct.setCreatedAt(LocalDateTime.now());
            updatedProduct.setUpdatedAt(LocalDateTime.now());
            return productRepository.save(updatedProduct);
        });
    }
}

