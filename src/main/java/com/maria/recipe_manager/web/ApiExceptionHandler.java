package com.maria.recipe_manager.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    //pt 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req){

        Map<String,String> fieldErrors=ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (a,b)->a,
                        LinkedHashMap::new));

        Map<String,Object> body=new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status",400);
        body.put("error","Bad Request");
        body.put("message", "Validation failed");
        body.put("errors", fieldErrors);
        body.put("path", req.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req){

        Map<String,Object> body=new LinkedHashMap<>();
        body.put("timestamp",Instant.now());
        body.put("timestamp", Instant.now());
        body.put("status", 400);
        body.put("error", "Bad Request");
        // pentru enum/JSON invalid
        body.put("message", ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : "Malformed JSON or invalid value");
        body.put("path", req.getRequestURI());

        return ResponseEntity.badRequest().body(body);
    }

    //pt 404
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String,Object>> handleNotFound(NotFoundException ex,HttpServletRequest req){

        Map<String,Object> body=new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        body.put("path", req.getRequestURI());

        return ResponseEntity.status(404).body(body);
    }
}
