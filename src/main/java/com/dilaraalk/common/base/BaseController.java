package com.dilaraalk.common.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class BaseController {

    protected <T> ResponseEntity<T> ok(T body) {
        return ResponseEntity.ok(body);
    }

    protected <T> ResponseEntity<T> created(T body) {
        return new ResponseEntity<>(body, HttpStatus.CREATED);
    }

    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }
}