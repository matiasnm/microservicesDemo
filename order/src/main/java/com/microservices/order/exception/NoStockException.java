package com.microservices.order.exception;

public class NoStockException extends RuntimeException {
   public NoStockException(String sku) {
      super("Product SKU: %s is not in stock.".formatted(sku));
   }

   public static NoStockException of(String sku) {
      return new NoStockException(sku);
   }
}