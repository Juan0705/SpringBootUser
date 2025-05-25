package com.juan.spring.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.juan.spring.entities.User;

public interface UserService {

    // GET - Obtener todos los usuarios
    List<User> getAllUsers();

    // GET - Obtener un usuario por ID
    Optional<User> getUserById(UUID id);

    // GET - Obtener un usuario por correo
    Optional<User> getUserByEmail(String email);

    // POST - Crear un nuevo usuario
    User createUser(User user);

    // PUT - Actualizar un usuario completo
    User updateUser(UUID id, User user);

    // PATCH - Actualizar parcialmente un usuario
    User partialUpdateUser(UUID id, User user);

    // DELETE - Eliminar un usuario
    void deleteUser(UUID id);

    // Método adicional para validar si un usuario existe
    boolean existsById(UUID id);

}
