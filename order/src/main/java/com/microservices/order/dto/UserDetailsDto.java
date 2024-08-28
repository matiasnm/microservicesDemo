package com.microservices.order.dto;

import jakarta.validation.constraints.NotNull;

public record UserDetailsDto(
    @NotNull(message = "Incomplete attribute: 'Email'.") String email,
    @NotNull(message = "Incomplete attribute: 'First Name'.") String firstName,
    @NotNull(message = "Incomplete attribute: 'Last Name'.") String lastName) {
}