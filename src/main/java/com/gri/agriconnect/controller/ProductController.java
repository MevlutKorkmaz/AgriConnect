package com.gri.agriconnect.controller;

import com.gri.agriconnect.model.Product;
import com.gri.agriconnect.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@Validated
@Tag(name = "Product", description = "API for managing products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product", description = "Adds a new product to the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        logger.info("Creating a new product");
        try {
            Product createdProduct = productService.saveProduct(product);
            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @Operation(summary = "Get all products", description = "Fetches all products in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products fetched",
                    content = @Content(schema = @Schema(implementation = Product.class)))
    })
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        logger.info("Fetching all products");
        List<Product> products = productService.getAllProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get products by supplier ID", description = "Fetches products by their supplier ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products fetched",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "No products found")
    })
    @GetMapping("/supplier/{supplierId}")
    public ResponseEntity<List<Product>> getProductsBySupplierId(@PathVariable String supplierId) {
        logger.info("Fetching products for supplier ID: {}", supplierId);
        List<Product> products = productService.getProductsBySupplierId(supplierId);
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found for supplier ID: " + supplierId);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Get product by ID", description = "Fetches a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product fetched",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable String productId) {
        logger.info("Fetching product with ID: {}", productId);
        Optional<Product> product = productService.getProductById(productId);
        return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + productId));
    }

    @Operation(summary = "Delete product", description = "Deletes a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        logger.info("Deleting product with ID: {}", productId);
        try {
            productService.deleteProduct(productId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Update product", description = "Updates a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId, @Valid @RequestBody Product product) {
        logger.info("Updating product with ID: {}", productId);
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Patch product", description = "Partially updates a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product patched",
                    content = @Content(schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PatchMapping("/{productId}")
    public ResponseEntity<Product> patchProduct(@PathVariable String productId, @RequestBody Product product) {
        logger.info("Patching product with ID: {}", productId);
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Search products by tag", description = "Fetch products by a specific tag.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "No products found for the tag")
    })
    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<Product>> searchProductsByTag(
            @Parameter(description = "Tag to search products by") @PathVariable String tag) {
        logger.info("Searching products by tag: {}", tag);
        List<Product> products = productService.searchProductsByTag(tag);
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found for the tag: " + tag);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Search products by name or description", description = "Fetch products by name or description.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Product.class))),
            @ApiResponse(responseCode = "404", description = "No products found for the search text")
    })
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByNameOrDescription(
            @Parameter(description = "Search text for name or description") @RequestParam String searchText) {
        logger.info("Searching products by name or description: {}", searchText);
        List<Product> products = productService.searchProductsByNameOrDescription(searchText);
        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No products found for the search text: " + searchText);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @Operation(summary = "Like a product", description = "Like a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product liked successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/{productId}/like")
    public ResponseEntity<Product> likeProduct(
            @Parameter(description = "ID of the product to like") @PathVariable String productId) {
        logger.info("Liking product with ID: {}", productId);
        try {
            Product likedProduct = productService.likeProduct(productId);
            return new ResponseEntity<>(likedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Unlike a product", description = "Unlike a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product unliked successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/{productId}/unlike")
    public ResponseEntity<Product> unlikeProduct(
            @Parameter(description = "ID of the product to unlike") @PathVariable String productId) {
        logger.info("Unliking product with ID: {}", productId);
        try {
            Product unlikedProduct = productService.unlikeProduct(productId);
            return new ResponseEntity<>(unlikedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Increment share count", description = "Increment the share count of a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Share count incremented successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/{productId}/share")
    public ResponseEntity<Void> incrementShareCount(
            @Parameter(description = "ID of the product to increment share count") @PathVariable String productId) {
        logger.info("Incrementing share count for product with ID: {}", productId);
        try {
            productService.incrementShareCount(productId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @Operation(summary = "Add images to product", description = "Adds multiple images to a product by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Images added successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/{productId}/images")
    public ResponseEntity<Void> addImages(
            @Parameter(description = "ID of the product to add images to") @PathVariable String productId,
            @RequestParam("files") MultipartFile[] images) {
        logger.info("Adding images to product with ID: {}", productId);
        try {
            productService.addImages(productId, images);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to upload images");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        logger.error("Error occurred: {}", ex.getMessage());
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}
