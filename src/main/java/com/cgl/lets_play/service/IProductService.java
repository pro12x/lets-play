package com.cgl.lets_play.service;

import com.cgl.lets_play.dto.ProductDto;

import java.util.List;

public interface IProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(String id);
    List<ProductDto> getProductsByUserId(String userId);
    ProductDto createProduct(ProductDto productDto);
    ProductDto updateProduct(String id, ProductDto productDto);
    void deleteProduct(String id);
}
