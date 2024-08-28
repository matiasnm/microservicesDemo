package com.microservices.order.service;

import com.microservices.order.client.InventoryClient;
import com.microservices.order.dto.OrderRequestDto;
import com.microservices.order.dto.OrderResponseDto;
import com.microservices.order.entity.Order;
import com.microservices.order.event.OrderPlaceEvent;
import com.microservices.order.mapper.*;
import com.microservices.order.repository.OrderRepository;
import com.microservices.order.exception.FallbackException;
import com.microservices.order.exception.InventoryClientException;
import com.microservices.order.exception.NoResultsException;
import com.microservices.order.exception.NoStockException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final OrderMapper orderMapper;
    private final OrderResponseDtoMapper responseMapper;
    private final InventoryClient inventoryClient;
    private final KafkaTemplate<String, OrderPlaceEvent> kafkaTemplate;

    private boolean checkStock(OrderRequestDto request) {
        try {
            return inventoryClient.isInStock(request.sku(), request.quantity());
        } catch (FallbackException fe) {
            throw fe;
        } catch (Exception ex) {
            throw InventoryClientException.of(ex.getMessage());
        }
    }

    @Transactional
    public String createOrder(OrderRequestDto request) {
        if (checkStock(request)) {
            Order order = orderMapper.apply(request);
            Order savedOrder = repository.save(order);
            // send msg to Kafka topic
            String orderId = savedOrder.getId().toString();
            OrderPlaceEvent event = new OrderPlaceEvent(orderId, request.product_id());
            log.info("START: sending 'OrderPlacedEvent {}' to Kafka topic ORDER PLACED");
            kafkaTemplate.send("ORER PLACED:", event);
            log.info("END: sending 'OrderPlacedEvent {}' to Kafka topic ORDER PLACED");

            return orderId;
        } else {
            throw NoStockException.of(request.sku());
        }
    }

    @Transactional(readOnly = true)
    public OrderResponseDto readOrder(String id) {
        Order dbOrder = getOrder(id);
        return responseMapper.apply(dbOrder);
    }

    @Transactional
    public void updateOrder(OrderRequestDto request, String id) { 
        Order dbOrder = getOrder(id);
        repository.saveAndFlush(orderMapper.apply(request, dbOrder));
    }

    @Transactional(readOnly = true)
    public Page<OrderResponseDto> readAll(Pageable pageable) {
        Page<Order> orders = repository.findAll(PageRequest.of(
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSortOr(Sort.by(Sort.Direction.ASC, "sku")))
        );
        if (orders.getContent().isEmpty()) {
            throw NoResultsException.of("Orders");
        }
        Page<OrderResponseDto> ordersDto = orders.map(order -> responseMapper.apply(order));
        return ordersDto;
    }

    private Order getOrder(String id) throws NoResultsException {
        try {
            Order dbOrder = repository.findById(id)
                .orElseThrow(() -> NoResultsException.of(id));
            return dbOrder;
        }
        catch(IllegalArgumentException ex)
        {
            throw NoResultsException.of(id);
        }
    }
}