package com.microservices.inventory.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record InventoryRequestDto(
    @NotEmpty(message = "Incomplete attribute: 'SKU'.") String sku,
    @NotNull(message = "Incomplete attribute: 'Quantity'.") Integer quantity) {}