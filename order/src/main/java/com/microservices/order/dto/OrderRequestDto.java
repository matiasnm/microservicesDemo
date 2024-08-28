package com.microservices.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
    @NotNull(message = "Incomplete attribute: 'Id'.") String product_id,
    @NotNull(message = "Incomplete attribute: 'SKU'.") String sku,
    @NotNull(message = "Incomplete attribute: 'Quantity'.") Integer quantity,
    @Valid UserDetailsDto userDetails) {
}