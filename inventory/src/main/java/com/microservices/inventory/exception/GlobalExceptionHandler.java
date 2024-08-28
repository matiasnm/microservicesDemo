package com.microservices.inventory.exception;

import java.net.URI;
import java.time.Instant;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
  
    @ExceptionHandler(NoResultsException.class)
    ProblemDetail handleNoResultsException(NoResultsException e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
        problemDetail.setTitle("No results");
        problemDetail.setType(URI.create("/")); //https://api.ejemplo.com/errors/not-found
        problemDetail.setProperty("errorCategory", "Repository");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail handleNotFoundException(ConstraintViolationException e) {

        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        StringBuilder errorMsg = new StringBuilder();
        for (ConstraintViolation<?> violation : violations) {
            String prop = violation.getPropertyPath().toString();
            prop = prop.substring(0,1).toUpperCase().concat(prop.substring(1));
            errorMsg
                .append(prop).append(" ")
                .append(violation.getMessage()).append(".");
        }
        String msg = errorMsg.toString();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, msg);
        problemDetail.setTitle("Not found");
        problemDetail.setType(URI.create("/"));
        problemDetail.setProperty("errorCategory", "Server");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

   /*
    // sacar extends ResponseEntityExceptionHandler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, msg);
        problemDetail.setTitle("Invalid Argument");
        problemDetail.setType(URI.create("/"));
        problemDetail.setProperty("errorCategory", "Product Request");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllGenericExceptions(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problemDetail.setTitle("Generic Exception");
        problemDetail.setType(URI.create("/"));
        problemDetail.setProperty("errorCategory", "Generic Exception");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    }

    @ExceptionHandler(RuntimeException.class)
    public ProblemDetail handleAllRuntimeExceptions(Exception e) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        problemDetail.setTitle("Generic Exception");
        problemDetail.setType(URI.create("/"));
        problemDetail.setProperty("errorCategory", "Generic Exception");
        problemDetail.setProperty("timestamp", Instant.now());
        return problemDetail;
    } */

}