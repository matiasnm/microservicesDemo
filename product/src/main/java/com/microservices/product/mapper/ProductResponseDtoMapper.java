package com.microservices.product.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.microservices.product.dto.ProductResponseDto;
import com.microservices.product.entity.Product;

@Service
public class ProductResponseDtoMapper implements Function<Product, ProductResponseDto> {

    @Override
    public ProductResponseDto apply(Product product) {
        return new ProductResponseDto(
            product.getId(),
            product.getSku(),
            product.getPrice(),
            product.getCreatedAt(),
            product.getUpdatedAt());
    }
    
}
