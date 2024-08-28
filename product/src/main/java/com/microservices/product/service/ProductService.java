package com.microservices.product.service;

import com.microservices.product.dto.ProductRequestDto;
import com.microservices.product.dto.ProductResponseDto;
import com.microservices.product.entity.Product;
import com.microservices.product.exception.InvalidSkuException;
import com.microservices.product.exception.NoResultsException;
import com.microservices.product.mapper.*;
import com.microservices.product.repository.ProductRepository;

import lombok.AllArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductMapper productMapper;
    private final ProductResponseDtoMapper responseMapper;

    @Transactional
    public String createProduct(ProductRequestDto request) {
        if (repository.existsBySku(request.sku()) == true) {
            throw InvalidSkuException.of(request.sku());
        }
        Product product = productMapper.apply(request);
        Product savedProduct = repository.save(product);
        return savedProduct.getId().toString();
    }

    @Transactional(readOnly = true)
    public ProductResponseDto readProduct(String id) {
        Product dbProduct = getProduct(id);
        return responseMapper.apply(dbProduct);
    }

    @Transactional
    public void updateProduct(ProductRequestDto request, String id) { 
        Product dbProduct = getProduct(id);
        repository.save(productMapper.apply(request, dbProduct));
    }

    @Transactional
    public void deleteProduct(String id) {
        Product dbProduct = getProduct(id);
        repository.deleteById(dbProduct.getId());
    }

    
    @Transactional(readOnly = true)
    public Page<ProductResponseDto> readAll(Pageable pageable) {
        Page<Product> products = repository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "sku")))
        );
        if (products.getContent().isEmpty()) {
            throw NoResultsException.of("Products");
        }
        Page<ProductResponseDto> productsDto = products.map(product -> responseMapper.apply(product));
        return productsDto;
    }

    private Product getProduct(String id) throws NoResultsException {
        try {
            Product dbProduct = repository.findById(id)
                .orElseThrow(() -> NoResultsException.of(id));
            return dbProduct;
        }
        catch(IllegalArgumentException ex)
        {
            throw NoResultsException.of(id);
        }
    }
}