package com.microservices.product.repository;

import com.microservices.product.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface ProductRepository extends MongoRepository<Product, String> {

    public boolean existsBySku(String sku);

    public Page<Product> findAll(Pageable pageable);
}
