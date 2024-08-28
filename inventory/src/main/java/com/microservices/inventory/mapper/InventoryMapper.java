package com.microservices.inventory.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.microservices.inventory.dto.InventoryRequestDto;
import com.microservices.inventory.entity.Inventory;

@Service
public class InventoryMapper implements Function<InventoryRequestDto, Inventory> {

    @Override
    public Inventory apply(InventoryRequestDto request) {
        return Inventory.builder()
            .sku(request.sku())
            .stock(request.quantity())
            .build();
    }

    public Inventory apply(InventoryRequestDto request, Inventory inventory) {
        inventory.setSku(request.sku());
        inventory.setStock(request.quantity());
        return inventory;
    }
    
}