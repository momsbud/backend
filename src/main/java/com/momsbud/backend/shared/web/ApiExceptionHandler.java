package com.momsbud.backend.shared.web;

import com.momsbud.backend.coreidentity.service.impl.UserQueryServiceImpl.NotFound;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<?> notFound(NotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new Problem("not_found", e.getMessage())
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> badRequest(HttpMessageNotReadableException e) {
        String msg = (e.getMessage() != null) ? e.getMessage() : e.getClass().getSimpleName();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Problem("bad_request", msg));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> internal(Throwable e) {
        // Return minimal error info to aid debugging
        String msg = (e.getMessage() != null) ? e.getMessage() : e.getClass().getSimpleName();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new Problem("internal_error", msg)
        );
    }

    record Problem(String code, String message) {}
}
