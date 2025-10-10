package com.momsbud.backend.shared.web;

import com.momsbud.backend.coreidentity.service.impl.UserQueryServiceImpl.NotFound;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFound.class)
    public ResponseEntity<?> notFound(NotFound e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new Problem("not_found", e.getMessage())
        );
    }

    record Problem(String code, String message) {}
}
