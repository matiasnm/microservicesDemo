package com.microservices.inventory.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.microservices.inventory.dto.InventoryResponseDto;
import com.microservices.inventory.entity.Inventory;

@Service
public class InventoryResponseDtoMapper implements Function<Inventory, InventoryResponseDto> {

    @Override
    public InventoryResponseDto apply(Inventory inventory) {
        return new InventoryResponseDto(
            inventory.getSku(),
            inventory.getStock(),
            inventory.getCreatedAt(),
            inventory.getUpdatedAt()
        );
    }
    
}