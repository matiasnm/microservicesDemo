package com.microservices.product.dto;

import java.time.OffsetDateTime;

public record ProductResponseDto(String id, String SKU, Float price, OffsetDateTime created_at, OffsetDateTime updated_at) {
}