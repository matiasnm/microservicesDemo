package com.microservices.inventory.exception;

public class NoResultsException extends RuntimeException {
   public NoResultsException(String id) {
      super("No results for: %s".formatted(id));
   }

   public static NoResultsException of(String id) {
      return new NoResultsException(id);
   }
}