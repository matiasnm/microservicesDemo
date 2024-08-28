package com.microservices.order.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.microservices.order.dto.OrderRequestDto;
import com.microservices.order.entity.Order;

@Service
public class OrderMapper implements Function<OrderRequestDto, Order> {

    @Override
    public Order apply(OrderRequestDto request) {
        return Order.builder()
            .product_id(request.product_id())
            .sku(request.sku())
            .quantity(request.quantity())
            .build();
    }

    public Order apply(OrderRequestDto request, Order order) {
        order.setProduct_id(request.product_id());
        order.setSku(request.sku());
        order.setQuantity(request.quantity());   
        return order;
    }
    
}
