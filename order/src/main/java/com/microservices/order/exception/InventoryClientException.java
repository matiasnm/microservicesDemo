package com.microservices.order.exception;

public class InventoryClientException extends RuntimeException {
   public InventoryClientException(String msg) {
      super(msg);
   }

   public static InventoryClientException of(String msg) {
      return new InventoryClientException(msg);
   }
}