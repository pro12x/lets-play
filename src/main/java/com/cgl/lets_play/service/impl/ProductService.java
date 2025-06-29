package com.cgl.lets_play.service.impl;

import com.cgl.lets_play.dto.ProductDto;
import com.cgl.lets_play.exception.ResourceNotFoundException;
import com.cgl.lets_play.exception.UnauthorizedException;
import com.cgl.lets_play.model.Product;
import com.cgl.lets_play.model.User;
import com.cgl.lets_play.repository.ProductRepository;
import com.cgl.lets_play.repository.UserRepository;
import com.cgl.lets_play.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
// @RequiredArgsConstructor
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;
    //private final ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;
    //private final UserRepository userRepository;

    @Override
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return convertToDto(product);
    }

    @Override
    public List<ProductDto> getProductsByUserId(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }

        return productRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        String currentUserEmail = getCurrentUserEmail();
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = Product.builder()
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .userId(user.getId())
                .build();

        Product savedProduct = productRepository.save(product);
        return convertToDto(savedProduct);
    }

    @Override
    public ProductDto updateProduct(String id, ProductDto productDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        checkAuthorization(existingProduct);

        existingProduct.setName(productDto.getName() != null ? productDto.getName() : existingProduct.getName());
        existingProduct.setDescription(productDto.getDescription() != null ? productDto.getDescription() : existingProduct.getDescription());
        existingProduct.setPrice(productDto.getPrice() != null ? productDto.getPrice() : existingProduct.getPrice());

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDto(updatedProduct);
    }

    @Override
    public void deleteProduct(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        checkAuthorization(product);

        productRepository.deleteById(id);
    }

    private void checkAuthorization(Product product) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String currentUserEmail = ((UserDetails) authentication.getPrincipal()).getUsername();
            User currentUser = userRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (currentUser.getRole().equals("ROLE_ADMIN")) return;

            if (!product.getUserId().equals(currentUser.getId())) {
                throw new UnauthorizedException("You are not authorized to modify this product");
            }
        } else {
            throw new UnauthorizedException("Authentication required");
        }
    }

    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) authentication.getPrincipal()).getUsername();
        }

        throw new UnauthorizedException("Authentication required");
    }

    private ProductDto convertToDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .userId(product.getUserId())
                .build();
    }
}
