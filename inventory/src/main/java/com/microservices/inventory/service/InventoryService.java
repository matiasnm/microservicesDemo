package com.microservices.inventory.service;

import com.microservices.inventory.dto.InventoryRequestDto;
import com.microservices.inventory.dto.InventoryResponseDto;
import com.microservices.inventory.entity.Inventory;
import com.microservices.inventory.exception.NoResultsException;
import com.microservices.inventory.mapper.*;
import com.microservices.inventory.repository.InventoryRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;
    private final InventoryMapper inventoryMapper;
    private final InventoryResponseDtoMapper responseMapper;

    @Transactional
    public String createInventory(InventoryRequestDto request) {
        Inventory inventory = inventoryMapper.apply(request);
        Inventory savedInventory = repository.save(inventory);
        return savedInventory.getId().toString();
    }

    @Transactional(readOnly = true)
    public InventoryResponseDto readInventory(String id) {
        Inventory dbInventory = getInventory(id);
        return responseMapper.apply(dbInventory);
    }

    @Transactional
    public void updateInventory(InventoryRequestDto request, String id) { 
        Inventory dbInventory = getInventory(id);
        repository.save(inventoryMapper.apply(request, dbInventory));
    }

    @Transactional
    public void deleteInventory(String id) {
        Inventory dbInventory = getInventory(id);
        repository.deleteById(dbInventory.getId());
    }

    private Inventory getInventory(String id) throws NoResultsException {
        try {
            Inventory dbInventory = repository.findById(id)
                .orElseThrow(() -> NoResultsException.of(id));
            return dbInventory;
        }
        catch(IllegalArgumentException ex)
        {
            throw NoResultsException.of(id);
        }
    }

    public Boolean isInStock(String sku, Integer stock) {
        return repository.existsBySkuAndStockIsGreaterThanEqual(sku, stock);
    }
}