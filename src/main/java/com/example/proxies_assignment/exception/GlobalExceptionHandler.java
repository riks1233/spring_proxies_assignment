package com.example.proxies_assignment.exception;

import java.util.*;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.example.proxies_assignment.general.Null;
import com.example.proxies_assignment.http_response.ErrorMessageResponse;
import com.example.proxies_assignment.http_response.ErrorResponse;
import com.example.proxies_assignment.http_response.Response;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Response<Null> handleUnexpectedException(Exception exception, WebRequest request) {
        // TODO: Log the value and respond with a generic error message.
        // End users should not be able to see unexpected server-side error messages.
        return new ErrorMessageResponse("Something unexpected happened.", request.getDescription(false));
    }

    // Handle bad/invalid values from requests.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<Map<String, String>> handleValidationException(MethodArgumentNotValidException exception, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ErrorResponse<Map<String, String>>("Field validation errors.", request.getDescription(false), errors);
    }

    // Handle generic error exceptions (thrown manually to send a message to the user.)
    @ExceptionHandler(GenericErrorException.class)
    public Response<Null> handleUnexpectedException(GenericErrorException exception, WebRequest request) {
        return new ErrorMessageResponse(exception.getMessage(), request.getDescription(false));
    }

    // Handle malformed JSON requests.
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response<Null> handleParsingException(HttpMessageNotReadableException exception, WebRequest request) {
        return new ErrorMessageResponse(String.format("Received an invalid JSON body: %s", exception.getMessage()), request.getDescription(false));
    }

    // Handle unique constraint violations.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Response<Null> handleParsingException(DataIntegrityViolationException exception, WebRequest request) {
        String constraintName = "unknown";
        if ((exception.getCause() != null) && (exception.getCause() instanceof ConstraintViolationException)) {
            constraintName = ((ConstraintViolationException) exception.getCause()).getConstraintName();
        }
        return new ErrorMessageResponse(String.format("Violated constraint: %s", constraintName), request.getDescription(false));
    }

    // Handle missing request params.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Response<Null> handleParsingException(MissingServletRequestParameterException exception, WebRequest request) {
        return new ErrorMessageResponse(exception.getMessage(), request.getDescription(false));
    }
}
