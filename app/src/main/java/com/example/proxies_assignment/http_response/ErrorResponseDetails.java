package com.example.proxies_assignment.http_response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorResponseDetails<T> extends ResponseDetails<T> {
    private String errorMessage;
    private String endpoint;

    public ErrorResponseDetails(String errorMessage, String endpoint, T data) {
        super(false, data);
        this.errorMessage = errorMessage;
        this.endpoint = endpoint;
    }

    @JsonProperty("error_message")
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

}
