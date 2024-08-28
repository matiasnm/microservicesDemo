package com.microservices.order.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

import com.microservices.order.exception.FallbackException;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

public interface InventoryClient {

    Logger log = LoggerFactory.getLogger(InventoryClient.class);

    @GetExchange("/api/v3/inventory")
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod")
    @Retry(name = "inventory")
    boolean isInStock(@RequestParam("sku") String sku, @RequestParam("quantity") Integer quantity);
    
    // FallbackException is oOnly visible through Order service directly
    // Gateway will use a default 503 error fallback for this!
    default boolean fallbackMethod(String code, Integer quantity, Throwable throwable) {
        String msg = "Cannot get inventory for skucode " + code + ", failure reason: " + throwable.getMessage();
        log.info(msg);
        throw FallbackException.of(msg);
    }
}