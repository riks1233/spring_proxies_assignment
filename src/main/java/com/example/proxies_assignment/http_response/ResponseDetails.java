package com.example.proxies_assignment.http_response;

// See Response<T> for more information.
public abstract class ResponseDetails<T> {
    private boolean success;
    private T data;

    public ResponseDetails(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
