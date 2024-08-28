package com.microservices.product.exception;

public class InvalidSkuException extends RuntimeException {
    public InvalidSkuException(String sku) {
        super("Invalid SKU: %s".formatted(sku));
     }
  
     public static InvalidSkuException of(String sku) {
        return new InvalidSkuException(sku);
     }
}
