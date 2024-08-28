package com.microservices.inventory.controller;

import com.microservices.inventory.service.InventoryService;
import com.microservices.inventory.dto.InventoryResponseDto;
import com.microservices.inventory.dto.InventoryRequestDto;

import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("api/v3/inventory")
public class InventoryController {

    private final InventoryService service;

    @GetMapping
    public ResponseEntity<Boolean> isInStock(@RequestParam("sku") String sku, @RequestParam("quantity") Integer quantity) {
        Boolean inStock = service.isInStock(sku, quantity);
        log.info("GET -> In Stock?: {}", inStock);
        return ResponseEntity.ok(inStock);
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Valid InventoryRequestDto request, UriComponentsBuilder ucb) {
        log.info("CREATE -> New Inventory: {}", request);
        String id = service.createInventory(request);
        URI location = ucb
            .path("inventory/{id}")
            .buildAndExpand(id)
            .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<InventoryResponseDto> read(@PathVariable("id") String id) {
        log.info("READ -> Inventory ID: {}", id);
        return ResponseEntity.ok(service.readInventory(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Void> update(
        @RequestBody @Valid InventoryRequestDto request,
        @PathVariable("id") String id) 
    {
        log.info("UPDATE -> Inventory ID: {}", request);
        service.updateInventory(request, id);
        return ResponseEntity.noContent().build();
    }

}