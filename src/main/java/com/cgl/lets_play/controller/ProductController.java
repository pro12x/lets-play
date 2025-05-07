package com.cgl.lets_play.controller;

import com.cgl.lets_play.dto.ProductDto;
import com.cgl.lets_play.service.impl.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController
{
    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Get all products")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get product by ID")
    @ApiResponse(responseCode = "200", description = "Product retrieved successfully")
    public ResponseEntity<ProductDto> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get products by user ID", description = "Get products by user ID")
    @ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public ResponseEntity<List<ProductDto>> getProductsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(productService.getProductsByUserId(userId));
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Create a new product", description = "Create a new product")
    @ApiResponse(responseCode = "201", description = "Product created successfully")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        return new ResponseEntity<>(productService.createProduct(productDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update product", description = "Update product")
    @ApiResponse(responseCode = "200", description = "Product updated successfully")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String id, @Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.ok(productService.updateProduct(id, productDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Delete product", description = "Delete product")
    @ApiResponse(responseCode = "204", description = "Product deleted successfully")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
