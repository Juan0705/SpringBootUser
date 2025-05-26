package com.juan.spring.controllers;

import com.juan.spring.dto.JwtAuthResponse;
import com.juan.spring.dto.LoginDto;
import com.juan.spring.dto.SignUpDto;
import com.juan.spring.dto.ValidationErrorResponse;
import com.juan.spring.dto.ErrorMessage;
import com.juan.spring.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "API para la autenticación y registro de usuarios")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = JwtAuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de inicio de sesión inválidos",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
        @Parameter(description = "Credenciales de inicio de sesión", required = true)
        @RequestBody LoginDto loginDto) {
        try {
            JwtAuthResponse response = authService.login(loginDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(e.getMessage()));
        }
    }

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro exitoso",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = JwtAuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Datos de registro inválidos",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "El correo ya está registrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(
        @Parameter(description = "Datos de registro del usuario", required = true)
        @RequestBody SignUpDto signUpDto) {
        try {
            JwtAuthResponse response = authService.register(signUpDto);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(e.getMessage()));
        }
    }
}
