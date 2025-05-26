package com.juan.spring.services;

import com.juan.spring.dto.LoginDto;
import com.juan.spring.dto.SignUpDto;
import com.juan.spring.dto.ValidationErrorResponse;
import com.juan.spring.dto.JwtAuthResponse;
import com.juan.spring.entities.User;
import com.juan.spring.repositories.UserRepository;
import com.juan.spring.security.JwtTokenProvider;
import com.juan.spring.validation.ValidationPatterns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    @Transactional
    public JwtAuthResponse login(LoginDto loginDto) {
        ValidationErrorResponse validationErrors = validateLoginData(loginDto);
        if (!validationErrors.getErrors().isEmpty()) {
            throw new IllegalArgumentException(validationErrors.getErrors().get(0));
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDto.getCorreo(),
                loginDto.getContrasena()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generarToken(authentication);
        
        User user = userRepository.findByCorreo(loginDto.getCorreo())
            .orElseThrow(() -> new IllegalStateException("Usuario no encontrado"));
        user.setToken(jwt);
        user.setUltimoLogin(LocalDateTime.now());
        userRepository.save(user);
        
        return new JwtAuthResponse(jwt);
    }

    @Override
    @Transactional
    public JwtAuthResponse register(SignUpDto signUpDto) {
        ValidationErrorResponse validationErrors = validateSignUpData(signUpDto);
        if (!validationErrors.getErrors().isEmpty()) {
            throw new IllegalArgumentException(validationErrors.getErrors().get(0));
        }

        if (userRepository.findByCorreo(signUpDto.getCorreo()).isPresent()) {
            throw new IllegalStateException("El correo " + signUpDto.getCorreo() + " ya está registrado");
        }

        User user = new User();
        user.setNombre(signUpDto.getName());
        user.setCorreo(signUpDto.getCorreo());
        user.setContrasena(passwordEncoder.encode(signUpDto.getContrasena()));
        user.setEstaActivo(true);
        user.setCreado(LocalDateTime.now());
        user.setUltimoLogin(LocalDateTime.now());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            signUpDto.getCorreo(),
            signUpDto.getContrasena()
        );
        String jwt = tokenProvider.generarToken(authentication);
        user.setToken(jwt);

        userRepository.save(user);
        return new JwtAuthResponse(jwt);
    }

    @Override
    public ValidationErrorResponse validateLoginData(LoginDto loginDto) {
        ValidationErrorResponse validationErrors = new ValidationErrorResponse();

        if (loginDto.getCorreo() == null || loginDto.getCorreo().trim().isEmpty()) {
            validationErrors.addError(ValidationPatterns.EMAIL_ERROR_MESSAGE);
        } else if (!isValidEmail(loginDto.getCorreo())) {
            validationErrors.addError(ValidationPatterns.EMAIL_ERROR_MESSAGE);
        }

        if (loginDto.getContrasena() == null || loginDto.getContrasena().trim().isEmpty()) {
            validationErrors.addError(ValidationPatterns.PASSWORD_ERROR_MESSAGE);
        }

        return validationErrors;
    }

    @Override
    public ValidationErrorResponse validateSignUpData(SignUpDto signUpDto) {
        ValidationErrorResponse validationErrors = new ValidationErrorResponse();

        if (signUpDto.getCorreo() == null || signUpDto.getCorreo().trim().isEmpty()) {
            validationErrors.addError(ValidationPatterns.EMAIL_ERROR_MESSAGE);
        } else if (!isValidEmail(signUpDto.getCorreo())) {
            validationErrors.addError(ValidationPatterns.EMAIL_ERROR_MESSAGE);
        }

        if (signUpDto.getContrasena() == null || signUpDto.getContrasena().trim().isEmpty()) {
            validationErrors.addError(ValidationPatterns.PASSWORD_ERROR_MESSAGE);
        } else if (!isValidPassword(signUpDto.getContrasena())) {
            validationErrors.addError(ValidationPatterns.PASSWORD_ERROR_MESSAGE);
        }

        if (signUpDto.getName() == null || signUpDto.getName().trim().isEmpty()) {
            validationErrors.addError("El nombre es requerido");
        }

        return validationErrors;
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(ValidationPatterns.EMAIL_PATTERN, email);
    }

    private boolean isValidPassword(String password) {
        return Pattern.matches(ValidationPatterns.PASSWORD_PATTERN, password);
    }
} 