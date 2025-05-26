package com.juan.spring.services;

import com.juan.spring.dto.LoginDto;
import com.juan.spring.dto.SignUpDto;
import com.juan.spring.dto.ValidationErrorResponse;
import com.juan.spring.dto.JwtAuthResponse;

public interface AuthService {
    JwtAuthResponse login(LoginDto loginDto);
    JwtAuthResponse register(SignUpDto signUpDto);
    ValidationErrorResponse validateLoginData(LoginDto loginDto);
    ValidationErrorResponse validateSignUpData(SignUpDto signUpDto);
} 