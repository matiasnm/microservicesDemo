package com.microservices.order.dto;

import java.time.Instant;

public record OrderResponseDto(
    String product_id,
    String sku,
    Integer quantity,
    Instant created_at,
    Instant updated_at) {
}
