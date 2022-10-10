package com.example.proxies_assignment.http_response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

// Custom ResponseEntity object to be used with custom ResponseDetails to provide a generalized response structure.
public class Response<T> extends ResponseEntity<ResponseDetails<T>>{

    public Response(ResponseDetails<T> responseDetails, HttpStatus httpStatus) {
        super(responseDetails, httpStatus);
    }
}
