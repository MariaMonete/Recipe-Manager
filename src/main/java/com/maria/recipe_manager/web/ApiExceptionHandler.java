package com.maria.recipe_manager.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    //400-bean validation
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

        Map<String,Object> body = base(400, "Bad Request", "Validation failed", req);
        body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    //400-json gresit/enum gresit/tipuri gresite
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String,Object>> handleBadJson(HttpMessageNotReadableException ex, HttpServletRequest req){
        Map<String,Object> body = base(400, "Bad Request",
                ex.getMostSpecificCause() != null
                        ? ex.getMostSpecificCause().getMessage()
                        : "Malformed JSON or invalid value",
                req);
        return ResponseEntity.badRequest().body(body);
    }

    // 400 - validări pe parametri (ex: @Min pe @PathVariable/@RequestParam)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String,Object>> handleConstraintViolations(ConstraintViolationException ex, HttpServletRequest req){
        var errors = ex.getConstraintViolations().stream().collect(Collectors.toMap(
                v -> v.getPropertyPath().toString(),
                v -> v.getMessage(),
                (a,b)->a,
                LinkedHashMap::new
        ));
        Map<String,Object> body = base(400, "Bad Request", "Constraint violation", req);
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }

    // 400 - tip greșit la path/query (ex: id nu e Long)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String,Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req){
        String required = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "required type";
        String msg = "Parameter '%s' must be a valid %s".formatted(ex.getName(), required);
        return ResponseEntity.badRequest().body(base(400, "Bad Request", msg, req));
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

    // 400 - pentru IllegalArgumentException din servicii (ex: cantitate negativă, id nevalid logic)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req){
        return ResponseEntity.badRequest().body(base(400, "Bad Request", ex.getMessage(), req));
    }

    // 409 - încălcări de constrângeri DB (unique, FK, etc.)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest req){
        String msg = "Operation violates a database constraint (duplicate value or entity in use).";
        return ResponseEntity.status(409).body(base(409, "Conflict", msg, req));
    }

    // 500 - fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleAll(Exception ex, HttpServletRequest req){
        return ResponseEntity.status(500).body(base(500, "Internal Server Error", "Unexpected error", req));
    }

    private Map<String,Object> base(int status, String error, String message, HttpServletRequest req){
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status);
        body.put("error", error);
        body.put("message", message);
        body.put("path", req.getRequestURI());
        return body;
    }
}
