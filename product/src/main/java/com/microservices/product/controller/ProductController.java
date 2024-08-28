package com.microservices.product.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.microservices.product.dto.ProductRequestDto;
import com.microservices.product.dto.ProductResponseDto;
import com.microservices.product.service.ProductService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api/v3/products")
public class ProductController {

    private final ProductService service;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid ProductRequestDto request, UriComponentsBuilder ucb) {
        log.info("CREATE -> New Product: {}", request);
        String id = service.createProduct(request);
        URI location = ucb
            .path("products/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<ProductResponseDto> read(@PathVariable("id") String id) {
        log.info("READ -> Product ID: {}", id);
        return ResponseEntity.ok(service.readProduct(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> update(
        @RequestBody @Valid ProductRequestDto request,
        @PathVariable("id") String id) 
    {
        log.info("UPDATE -> Product ID: {}", request);
        service.updateProduct(request, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<Page<ProductResponseDto>> readAll(Pageable pageable) {
        log.info("READ ALL -> Products");
        return ResponseEntity.ok(service.readAll(pageable));
    }

}