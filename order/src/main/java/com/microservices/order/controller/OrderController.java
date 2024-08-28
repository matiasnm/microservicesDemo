package com.microservices.order.controller;

import com.microservices.order.service.OrderService;
import com.microservices.order.dto.OrderResponseDto;
import com.microservices.order.dto.OrderRequestDto;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api/v3/orders")
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid OrderRequestDto request, UriComponentsBuilder ucb) {
        log.info("CREATE -> New Order: {}", request);
        String id = service.createOrder(request);
        URI location = ucb
            .path("orders/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<OrderResponseDto> read(@PathVariable("id") String id) {
        log.info("READ -> Order ID: {}", id);
        return ResponseEntity.ok(service.readOrder(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> update(
        @RequestBody @Valid OrderRequestDto request,
        @PathVariable("id") String id) 
    {
        log.info("UPDATE -> Order ID: {}", id);
        service.updateOrder(request, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping()
    public ResponseEntity<Page<OrderResponseDto>> readAll(Pageable pageable) {
        log.info("READ ALL -> Orders");
        return ResponseEntity.ok(service.readAll(pageable));
    }
}