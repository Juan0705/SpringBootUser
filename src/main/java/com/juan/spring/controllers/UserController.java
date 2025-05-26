package com.juan.spring.controllers;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.juan.spring.services.UserService;
import com.juan.spring.dto.ErrorMessage;
import com.juan.spring.dto.UserDto;
import com.juan.spring.dto.UserCreateUpdateDto;
import com.juan.spring.dto.ValidationErrorResponse;
import com.juan.spring.entities.User;

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
        List<UserDto> users = userService.getAllUsers().stream()
            .map(userService::convertToDto)
            .collect(Collectors.toList());
            
        if (users.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("No se encontraron usuarios registrados"));
        }
        return ResponseEntity.ok(users);
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
            return ResponseEntity.ok(userService.convertToDto(userOpt.get()));
        }
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(new ErrorMessage("Usuario con ID " + id + " no encontrado"));
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
        try {
            UserDto createdUser = userService.createUserWithValidation(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(e.getMessage()));
        }
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
        try {
            UserDto updatedUser = userService.updateUserWithValidation(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(e.getMessage()));
        }
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
        try {
            UserDto updatedUser = userService.partialUpdateUserWithValidation(id, userDto);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ValidationErrorResponse(e.getMessage()));
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("no encontrado")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorMessage(e.getMessage()));
            }
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorMessage(e.getMessage()));
        }
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
