package com.juan.spring.controllers;

import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.juan.spring.entities.User;
import com.juan.spring.entities.Phone;
import com.juan.spring.services.UserService;
import com.juan.spring.dto.ErrorMessage;
import com.juan.spring.dto.UserDto;
import com.juan.spring.dto.UserCreateUpdateDto;
import com.juan.spring.dto.ValidationErrorResponse;
import com.juan.spring.dto.PhoneDto;
import com.juan.spring.security.JwtTokenProvider;
import com.juan.spring.validation.ValidationPatterns;
import org.springframework.security.crypto.password.PasswordEncoder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "API para la gestión de usuarios")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private boolean isValidEmail(String email) {
        return Pattern.matches(ValidationPatterns.EMAIL_PATTERN, email);
    }

    private boolean isValidPassword(String password) {
        return Pattern.matches(ValidationPatterns.PASSWORD_PATTERN, password);
    }

    private ValidationErrorResponse validateUserData(UserCreateUpdateDto userDto) {
        ValidationErrorResponse validationErrors = new ValidationErrorResponse();

        if (userDto.getCorreo() != null && !isValidEmail(userDto.getCorreo())) {
            validationErrors.addError(ValidationPatterns.EMAIL_ERROR_MESSAGE);
        }

        if (userDto.getContrasena() != null && !isValidPassword(userDto.getContrasena())) {
            validationErrors.addError(ValidationPatterns.PASSWORD_ERROR_MESSAGE);
        }

        return validationErrors;
    }

    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setNombre(user.getNombre());
        dto.setCorreo(user.getCorreo());
        dto.setEstaActivo(user.getEstaActivo());
        dto.setCreado(user.getCreado());
        dto.setModificado(user.getModificado());
        dto.setUltimoLogin(user.getUltimoLogin());
        dto.setToken(user.getToken());
        
        if (user.getTelefonos() != null) {
            List<PhoneDto> phoneDtos = user.getTelefonos().stream()
                .map(phone -> {
                    PhoneDto phoneDto = new PhoneDto();
                    phoneDto.setId(phone.getId());
                    phoneDto.setNumero(phone.getNumero());
                    phoneDto.setCodigoCiudad(phone.getCodigoCiudad());
                    phoneDto.setCodigoPais(phone.getCodigoPais());
                    return phoneDto;
                })
                .collect(Collectors.toList());
            dto.setTelefonos(phoneDtos);
        }
        
        return dto;
    }

    private User convertToEntity(UserCreateUpdateDto dto) {
        User user = new User();
        if (dto.getId() != null) {
            user.setId(dto.getId());
        }
        if (dto.getNombre() != null) {
            user.setNombre(dto.getNombre());
        }
        if (dto.getCorreo() != null) {
            user.setCorreo(dto.getCorreo());
        }
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            user.setContrasena(dto.getContrasena());
        }
        if (dto.getEstaActivo() != null) {
            user.setEstaActivo(dto.getEstaActivo());
        }
        if (dto.getCreado() != null) {
            user.setCreado(dto.getCreado());
        }
        if (dto.getModificado() != null) {
            user.setModificado(dto.getModificado());
        }
        if (dto.getUltimoLogin() != null) {
            user.setUltimoLogin(dto.getUltimoLogin());
        }
        if (dto.getToken() != null) {
            user.setToken(dto.getToken());
        }
        
        if (dto.getTelefonos() != null) {
            List<Phone> phones = dto.getTelefonos().stream()
                .map(phoneDto -> {
                    Phone phone = new Phone();
                    if (phoneDto.getId() != null) {
                        phone.setId(phoneDto.getId());
                    }
                    phone.setNumero(phoneDto.getNumero());
                    phone.setCodigoCiudad(phoneDto.getCodigoCiudad());
                    phone.setCodigoPais(phoneDto.getCodigoPais());
                    phone.setUser(user);
                    return phone;
                })
                .collect(Collectors.toList());
            user.setTelefonos(phones);
        }
        
        return user;
    }

    @Operation(summary = "Obtener todos los usuarios", description = "Retorna una lista de todos los usuarios registrados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios encontrada",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "No se encontraron usuarios",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("No se encontraron usuarios registrados"));
        }
        List<UserDto> userDtos = users.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
        return ResponseEntity.ok(userDtos);
    }

    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario específico basado en su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
        @Parameter(description = "ID del usuario a buscar", required = true)
        @PathVariable UUID id) {
        Optional<User> userOpt = userService.getUserById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(convertToDto(userOpt.get()));
        } else {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Usuario con ID " + id + " no encontrado"));
        }
    }

    @Operation(summary = "Crear nuevo usuario", description = "Crea un nuevo usuario con los datos proporcionados")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "El correo ya está registrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PostMapping
    public ResponseEntity<?> createUser(
        @Parameter(description = "Datos del usuario a crear", required = true)
        @RequestBody UserCreateUpdateDto userDto) {
        // Validar datos
        ValidationErrorResponse validationErrors = validateUserData(userDto);
        if (!validationErrors.getErrors().isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(validationErrors);
        }

        if (userService.getUserByEmail(userDto.getCorreo()).isPresent()) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("El correo " + userDto.getCorreo() + " ya está registrado"));
        }

        User user = convertToEntity(userDto);
        LocalDateTime now = LocalDateTime.now();
        
        // Encriptar la contraseña
        user.setContrasena(passwordEncoder.encode(user.getContrasena()));
        user.setEstaActivo(true);
        user.setUltimoLogin(now);

        // Generar token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getCorreo(),
            user.getContrasena()
        );
        String jwt = tokenProvider.generarToken(authentication);
        user.setToken(jwt);

        // Guardar usuario
        User createdUser = userService.createUser(user);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(convertToDto(createdUser));
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza todos los datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "409", description = "El correo ya está registrado para otro usuario",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
        @Parameter(description = "ID del usuario a actualizar", required = true)
        @PathVariable UUID id,
        @Parameter(description = "Datos actualizados del usuario", required = true)
        @RequestBody UserCreateUpdateDto userDto) {
        // Validar datos
        ValidationErrorResponse validationErrors = validateUserData(userDto);
        if (!validationErrors.getErrors().isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(validationErrors);
        }

        if (!userService.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Usuario con ID " + id + " no encontrado"));
        }

        // Verificar si el correo ya existe para otro usuario
        Optional<User> existingUserWithEmail = userService.getUserByEmail(userDto.getCorreo());
        if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(id)) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("El correo " + userDto.getCorreo() + " ya está registrado para otro usuario"));
        }

        User user = convertToEntity(userDto);
        User updatedUser = userService.updateUser(id, user);
        
        if (updatedUser == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Error al actualizar el usuario"));
        }
        
        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    @Operation(summary = "Actualización parcial de usuario", description = "Actualiza parcialmente los datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de usuario inválidos",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ValidationErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class))),
        @ApiResponse(responseCode = "409", description = "El correo ya está registrado para otro usuario",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateUser(
        @Parameter(description = "ID del usuario a actualizar", required = true)
        @PathVariable UUID id,
        @Parameter(description = "Datos parciales del usuario a actualizar", required = true)
        @RequestBody UserCreateUpdateDto userDto) {
        // Validar datos solo si se proporcionan
        if (userDto.getCorreo() != null || userDto.getContrasena() != null) {
            ValidationErrorResponse validationErrors = validateUserData(userDto);
            if (!validationErrors.getErrors().isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(validationErrors);
            }
        }

        if (!userService.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Usuario con ID " + id + " no encontrado"));
        }

        // Verificar si el correo ya existe para otro usuario
        if (userDto.getCorreo() != null) {
            Optional<User> existingUserWithEmail = userService.getUserByEmail(userDto.getCorreo());
            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(id)) {
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorMessage("El correo " + userDto.getCorreo() + " ya está registrado para otro usuario"));
            }
        }

        User user = convertToEntity(userDto);
        User updatedUser = userService.partialUpdateUser(id, user);
        
        if (updatedUser == null) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Error al actualizar el usuario"));
        }
        
        return ResponseEntity.ok(convertToDto(updatedUser));
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario existente por su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = ErrorMessage.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
        @Parameter(description = "ID del usuario a eliminar", required = true)
        @PathVariable UUID id) {
        if (!userService.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Usuario con ID " + id + " no encontrado"));
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
