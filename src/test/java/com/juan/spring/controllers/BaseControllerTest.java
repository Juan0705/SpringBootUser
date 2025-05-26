package com.juan.spring.controllers;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class BaseControllerTest {
    protected static final String USERS_URL = "/users";
    protected static final String AUTH_URL = "/api/auth";
} 