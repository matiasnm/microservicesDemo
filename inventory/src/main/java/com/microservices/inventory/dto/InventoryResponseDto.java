package com.microservices.inventory.dto;

import java.time.Instant;

public record InventoryResponseDto(
    String sku,
    Integer stock,
    Instant created_at,
    Instant updated_at) {}