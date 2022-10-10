package com.example.proxies_assignment.http_response;

import org.springframework.http.HttpStatus;

import com.example.proxies_assignment.general.Null;

public class ErrorMessageResponse extends Response<Null> {
    public ErrorMessageResponse(String errorMessage, String endpoint) {
        super(new ErrorResponseDetails<>(errorMessage, endpoint, null), HttpStatus.BAD_REQUEST);
    }
}
