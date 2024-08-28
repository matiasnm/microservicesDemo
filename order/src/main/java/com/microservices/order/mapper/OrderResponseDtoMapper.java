package com.microservices.order.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.microservices.order.dto.OrderResponseDto;
import com.microservices.order.entity.Order;

@Service
public class OrderResponseDtoMapper implements Function<Order, OrderResponseDto> {

    @Override
    public OrderResponseDto apply(Order order) {
        return new OrderResponseDto(
            order.getProduct_id(),
            order.getSku(),
            order.getQuantity(),
            order.getCreatedAt(),
            order.getUpdatedAt());
    }
    
}
