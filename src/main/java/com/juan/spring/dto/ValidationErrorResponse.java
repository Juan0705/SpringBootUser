package com.juan.spring.dto;

import java.util.ArrayList;
import java.util.List;

public class ValidationErrorResponse {
    private List<String> errors;

    public ValidationErrorResponse() {
        this.errors = new ArrayList<>();
    }

    public ValidationErrorResponse(String error) {
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }

    public ValidationErrorResponse(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        this.errors.add(error);
    }
} 