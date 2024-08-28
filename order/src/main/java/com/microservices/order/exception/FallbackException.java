package com.microservices.order.exception;

public class FallbackException extends RuntimeException {


   public FallbackException(String msg) {
      super(msg);
   }

   public static FallbackException of(String msg) {
      return new FallbackException(msg);
   }
}