package com.example.proxies_assignment.http_response;

import org.springframework.http.HttpStatus;

public class SuccessResponse<T> extends Response<T> {
    public SuccessResponse(T data) {
        super(new SuccessResponseDetails<>(data), HttpStatus.OK);
    }
}
