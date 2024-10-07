package com.abnamro.packageshippingservice.exception.handler;

import com.abnamro.packageshippingservice.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ShippingOrderNotFoundException.class)
    protected ResponseEntity<Object> handleConflict(ShippingOrderNotFoundException e) {
        log.error("Exception occurred while processing request. Error: {}", e.getMessage());
        return new ResponseEntity<>(constructErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, new HashMap<>()),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PackageNameAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handlePackageNameAlreadyExistsException(
            PackageNameAlreadyExistsException e) {
        log.error("Exception occurred while processing request. Error: {}", e.getMessage());
        return new ResponseEntity<>(constructErrorResponse(e.getMessage(), HttpStatus.CONFLICT, new HashMap<>()),HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleEmployeeNotFoundException(
            EmployeeNotFoundException e) {
        log.error("Exception occurred while validating request. Error: {}", e.getMessage());
        return new ResponseEntity<>(constructErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, new HashMap<>()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        String message = "Invalid Input Provided";
        HashMap<String, String> fieldErrors = Optional.of(e.getBindingResult())
                .map(result -> result.getFieldErrors().stream()
                        .collect(HashMap<String, String>::new, (map, error) ->
                                        map.put(error.getField(), error.getDefaultMessage()),
                                HashMap::putAll))
                .orElse(new HashMap<>());
        log.error("Exception occurred while validating request. Error: {}", e.getMessage());
        return new ResponseEntity<>(constructErrorResponse(message, HttpStatus.BAD_REQUEST, fieldErrors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException e) {
        log.error("Exception occurred while processing request. Error: {}", e.getMessage());
        return new ResponseEntity<>(constructErrorResponse("Invalid data: " + e.getMessage(), HttpStatus.BAD_REQUEST, new HashMap<>()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidEmployeeIdException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidEmployeeIdException(InvalidEmployeeIdException e) {
        log.error("Exception occurred while processing request. Error: {}", e.getMessage());
        return new ResponseEntity<>(constructErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, new HashMap<>()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiErrorResponse> handleMissingParams(MissingServletRequestParameterException e) {
        String message = String.format("Required parameter '%s' is missing", e.getParameterName());
        log.error("Exception occurred while processing request. Error: {}", message);
        return new ResponseEntity<>(constructErrorResponse(message, HttpStatus.BAD_REQUEST, new HashMap<>()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Exception occurred while processing request. Error: {}", e.getMessage());
        return new ResponseEntity<>(constructErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, new HashMap<>()), HttpStatus.BAD_REQUEST);
    }

    private ApiErrorResponse constructErrorResponse(String errorMessage, HttpStatus httpStatus, HashMap<String, String> errors) {
        return new ApiErrorResponse(httpStatus,errorMessage, errors);
    }
}
