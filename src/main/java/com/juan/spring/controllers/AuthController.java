package com.juan.spring.controllers;

import com.juan.spring.dto.JwtAuthResponse;
import com.juan.spring.dto.LoginDto;
import com.juan.spring.dto.SignUpDto;
import com.juan.spring.entities.User;
import com.juan.spring.repositories.UserRepository;
import com.juan.spring.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

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
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Operation(summary = "Iniciar sesión", description = "Autentica a un usuario y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = JwtAuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> authenticateUser(
        @Parameter(description = "Credenciales de inicio de sesión", required = true)
        @RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getCorreo(),
                        loginDto.getContrasena()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generarToken(authentication);
        
        // Actualizar el token y último login en la base de datos
        User user = userRepository.findByCorreo(loginDto.getCorreo()).orElseThrow();
        user.setToken(jwt);
        user.setUltimoLogin(LocalDateTime.now());
        userRepository.save(user);
        
        return ResponseEntity.ok(new JwtAuthResponse(jwt));
    }

    @Operation(summary = "Registrar usuario", description = "Registra un nuevo usuario y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Registro exitoso",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = JwtAuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "El correo ya está registrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(
        @Parameter(description = "Datos de registro del usuario", required = true)
        @RequestBody SignUpDto signUpDto) {
        // Verificar si el correo ya existe
        if (userRepository.existsByCorreo(signUpDto.getCorreo())) {
            return new ResponseEntity<>("El correo ya está registrado", HttpStatus.BAD_REQUEST);
        }

        // Crear nuevo usuario
        User user = new User();
        user.setNombre(signUpDto.getName());
        user.setCorreo(signUpDto.getCorreo());
        user.setContrasena(passwordEncoder.encode(signUpDto.getContrasena()));
        user.setEstaActivo(true);

        // Generar token y guardarlo
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            signUpDto.getCorreo(),
            signUpDto.getContrasena()
        );
        String jwt = tokenProvider.generarToken(authentication);
        user.setToken(jwt);

        userRepository.save(user);

        return ResponseEntity.ok(new JwtAuthResponse(jwt));
    }
}
