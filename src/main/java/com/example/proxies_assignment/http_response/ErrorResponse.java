package com.example.proxies_assignment.http_response;

import org.springframework.http.HttpStatus;

public class ErrorResponse<T> extends Response<T> {
    public ErrorResponse(String errorMessage, String endpoint, T data) {
        super(new ErrorResponseDetails<>(errorMessage, endpoint, data), HttpStatus.BAD_REQUEST);
    }
}
