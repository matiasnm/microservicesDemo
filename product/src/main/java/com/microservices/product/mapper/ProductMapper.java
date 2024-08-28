package com.microservices.product.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.microservices.product.dto.ProductRequestDto;
import com.microservices.product.entity.Product;

@Service
public class ProductMapper implements Function<ProductRequestDto, Product> {

    @Override
    public Product apply(ProductRequestDto request) {
        return Product.builder()
            .sku(request.sku())
            .price(request.price())
            .build();
    }

    public Product apply(ProductRequestDto request, Product product) {
        product.setSku(request.sku());
        product.setPrice(request.price());  
        return product;
    }
    
}
