package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Product;
import com.gri.agriconnect.model.User;
import com.gri.agriconnect.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @Autowired
    public ProductService(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }

    public Product saveProduct(Product product) {
        Optional<User> userOpt = userService.getUserById(product.getSupplierId());
        if (userOpt.isPresent()) {
            Product savedProduct = productRepository.save(product);
            userService.addProductToUser(product.getSupplierId(), savedProduct.getProductId());
            return savedProduct;
        } else {
            throw new IllegalArgumentException("User with ID " + product.getSupplierId() + " does not exist.");
        }
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
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            userService.removeProductFromUser(product.getSupplierId(), productId);
            productRepository.deleteById(productId);
        } else {
            throw new IllegalArgumentException("Product with ID " + productId + " does not exist.");
        }
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
            product.setCategoryTags(updatedProduct.getCategoryTags());
            product.setImageLinks(updatedProduct.getImageLinks());
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
