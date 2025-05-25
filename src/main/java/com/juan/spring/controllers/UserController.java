package com.juan.spring.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.juan.spring.entities.User;
import com.juan.spring.services.UserService;
import com.juan.spring.dto.ErrorMessage;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("No se encontraron usuarios registrados"));
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
            .<ResponseEntity<?>>map(user -> ResponseEntity.ok(user))
            .orElse(ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Usuario con ID " + id + " no encontrado")));
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userService.getUserByEmail(user.getCorreo()).isPresent()) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorMessage("El correo " + user.getCorreo() + " ya está registrado"));
        }
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody User user) {
        if (!userService.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Usuario con ID " + id + " no encontrado"));
        }
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateUser(@PathVariable UUID id, @RequestBody User user) {
        if (!userService.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Usuario con ID " + id + " no encontrado"));
        }
        User updatedUser = userService.partialUpdateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        if (!userService.existsById(id)) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage("Usuario con ID " + id + " no encontrado"));
        }
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
