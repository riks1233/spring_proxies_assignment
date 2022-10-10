package com.example.proxies_assignment.http_response;

public class SuccessResponseDetails<T> extends ResponseDetails<T> {
    public SuccessResponseDetails(T data) {
        super(true, data);
    }
}
