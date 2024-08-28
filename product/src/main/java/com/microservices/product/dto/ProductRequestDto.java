package com.microservices.product.dto;

import jakarta.validation.constraints.*;

public record ProductRequestDto(
    @Size(min = 3, message = "Product SKU should have at least 3 characters.")
    @NotEmpty(message = "Incomplete attribute: 'SKU'.") String sku, 
    @NotNull(message = "Incomplete attribute: 'price'.") Float price) {
}
