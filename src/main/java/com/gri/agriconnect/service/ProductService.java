package com.gri.agriconnect.service;

import com.gri.agriconnect.model.Product;
import com.gri.agriconnect.model.User;
import com.gri.agriconnect.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;
    private final ImageService imageService;

    @Autowired
    public ProductService(ProductRepository productRepository, UserService userService, ImageService imageService) {
        this.productRepository = productRepository;
        this.userService = userService;
        this.imageService = imageService;
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
            product.setDescription(updatedProduct.getDescription());
            product.setPrice(updatedProduct.getPrice());
            product.setSupplierId(updatedProduct.getSupplierId());
            product.setStockQuantity(updatedProduct.getStockQuantity());
            product.setFavoriteCount(updatedProduct.getFavoriteCount());
            product.setLikeCount(updatedProduct.getLikeCount());
            product.setCommentCount(updatedProduct.getCommentCount());
            product.setCommentIds(updatedProduct.getCommentIds());
            product.setCategoryTags(updatedProduct.getCategoryTags());
            product.setImageIds(updatedProduct.getImageIds());
            product.setIsPrivate(updatedProduct.getIsPrivate());
            product.setLocation(updatedProduct.getLocation());
            product.setShareCount(updatedProduct.getShareCount());
            product.setUpdatedAt(LocalDateTime.now());
            return productRepository.save(product);
        }).orElseGet(() -> {
            updatedProduct.setProductId(productId);
            updatedProduct.setCreatedAt(LocalDateTime.now());
            updatedProduct.setUpdatedAt(LocalDateTime.now());
            return productRepository.save(updatedProduct);
        });
    }

    public void addCategoryTag(String productId, String tag) {
        productRepository.findById(productId).ifPresent(product -> {
            product.addCategoryTag(tag);
            productRepository.save(product);
        });
    }

    public void removeCategoryTag(String productId, String tag) {
        productRepository.findById(productId).ifPresent(product -> {
            product.removeCategoryTag(tag);
            productRepository.save(product);
        });
    }

    public void addImage(String productId, MultipartFile image) throws IOException {
        productRepository.findById(productId).ifPresent(product -> {
            try {
                String imageId = imageService.uploadImageToFileSystem(image);
                product.addImage(imageId);
                productRepository.save(product);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        });
    }

    public void addImages(String productId, MultipartFile[] images) throws IOException {
        productRepository.findById(productId).ifPresent(product -> {
            for (MultipartFile image : images) {
                try {
                    String imageId = imageService.uploadImageToFileSystem(image);
                    product.addImage(imageId);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload image", e);
                }
            }
            productRepository.save(product);
        });
    }

    public void removeImage(String productId, String imageId) {
        productRepository.findById(productId).ifPresent(product -> {
            product.removeImage(imageId);
            productRepository.save(product);
        });
    }

    public Product likeProduct(String productId) {
        return productRepository.findById(productId).map(product -> {
            product.setLikeCount(product.getLikeCount() + 1);
            return productRepository.save(product);
        }).orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " does not exist."));
    }

    public Product unlikeProduct(String productId) {
        return productRepository.findById(productId).map(product -> {
            product.setLikeCount(Math.max(product.getLikeCount() - 1, 0));
            return productRepository.save(product);
        }).orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " does not exist."));
    }

    public void incrementShareCount(String productId) {
        productRepository.findById(productId).ifPresent(product -> {
            product.incrementShareCount();
            productRepository.save(product);
        });
    }

    // New method to search products by category tags
    public List<Product> searchProductsByTag(String tag) {
        return productRepository.findByCategoryTagsContaining(tag);
    }

    // New method to search products by name or description
    public List<Product> searchProductsByNameOrDescription(String searchText) {
        return productRepository.findByNameContainingOrDescriptionContaining(searchText, searchText);
    }
}
