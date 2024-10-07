package com.abnamro.packageshippingservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
public class ApiErrorResponse {

    private HttpStatus status;
    private String message;
    private HashMap<String, String> errors;

}
