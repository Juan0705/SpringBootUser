package com.juan.spring.validation;

public class ValidationPatterns {
    // Patrón para validar correo electrónico
    public static final String EMAIL_PATTERN = 
        "^[A-Za-z0-9+_.-]+@(.+)$";
    
    // Patrón para validar contraseña:
    // - Mínimo 8 caracteres
    // - Al menos una letra mayúscula
    // - Al menos una letra minúscula
    // - Al menos un número
    // - Al menos un carácter especial
    public static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    
    // Mensajes de error
    public static final String EMAIL_ERROR_MESSAGE = 
        "El correo electrónico debe tener un formato válido (ejemplo: usuario@dominio.com)";
    
    public static final String PASSWORD_ERROR_MESSAGE = 
        "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial";
} 