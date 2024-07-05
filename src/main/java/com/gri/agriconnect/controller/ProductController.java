package com.gri.agriconnect.controller;

import com.gri.agriconnect.model.Product;
import com.gri.agriconnect.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@Validated
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        logger.info("Creating a new product");
        Product createdProduct = productService.saveProduct(product);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        logger.info("Fetching products by category: {}", category);
        List<Product> products = productService.getProductsByCategory(category);
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found for category: " + category);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Product>> getProductsBySupplierId(@PathVariable String supplierId) {
        logger.info("Fetching products for supplier ID: {}", supplierId);
        List<Product> products = productService.getProductsBySupplierId(supplierId);
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found for supplier ID: " + supplierId);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        logger.info("Fetching product with ID: {}", productId);
        Optional<Product> product = productService.getProductById(productId);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + productId));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        logger.info("Deleting product with ID: {}", productId);
        Optional<Product> product = productService.getProductById(productId);
        if (product.isPresent()) {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + productId);
        }
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId, @Valid @RequestBody Product product) {
        logger.info("Updating product with ID: {}", productId);
        Optional<Product> existingProduct = productService.getProductById(productId);
        if (existingProduct.isPresent()) {
            Product updatedProduct = productService.updateProduct(productId, product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + productId);
        }
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<Product> patchProduct(@PathVariable String productId, @RequestBody Product product) {
        logger.info("Patching product with ID: {}", productId);
        Optional<Product> existingProduct = productService.getProductById(productId);
        if (existingProduct.isPresent()) {
            Product updatedProduct = existingProduct.get();
            if (product.getName() != null) {
                updatedProduct.setName(product.getName());
            }
            if (product.getCategory() != null) {
                updatedProduct.setCategory(product.getCategory());
            }
            if (product.getDescription() != null) {
                updatedProduct.setDescription(product.getDescription());
            }
            if (product.getPrice() != updatedProduct.getPrice()) {
                updatedProduct.setPrice(product.getPrice());
            }
            if (product.getSupplierId() != null) {
                updatedProduct.setSupplierId(product.getSupplierId());
            }
            if (product.getStockQuantity() != updatedProduct.getStockQuantity()) {
                updatedProduct.setStockQuantity(product.getStockQuantity());
            }
            if (product.getFavoriteCount() != updatedProduct.getFavoriteCount()) {
                updatedProduct.setFavoriteCount(product.getFavoriteCount());
            }
            if (product.getLikeCount() != updatedProduct.getLikeCount()) {
                updatedProduct.setLikeCount(product.getLikeCount());
            }
            if (product.getCommentCount() != updatedProduct.getCommentCount()) {
                updatedProduct.setCommentCount(product.getCommentCount());
            }
            if (product.getCommentIds() != null) {
                updatedProduct.setCommentIds(product.getCommentIds());
            }
            updatedProduct.setUpdatedAt(LocalDateTime.now());
            Product savedProduct = productService.saveProduct(updatedProduct);
            return new ResponseEntity<>(savedProduct, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + productId);
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}

