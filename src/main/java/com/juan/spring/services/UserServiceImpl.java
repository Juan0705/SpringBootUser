package com.juan.spring.services;

import com.juan.spring.entities.User;
import com.juan.spring.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return (List<User>) userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByCorreo(email);
    }

    @Override
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUser(UUID id, User user) {
        return userRepository.findById(id)
            .map(existingUser -> {
                // Preservar la fecha de creación original
                LocalDateTime fechaCreacion = existingUser.getCreado();
                
                // Actualizar todos los campos excepto id y fecha de creación
                existingUser.setNombre(user.getNombre());
                existingUser.setCorreo(user.getCorreo());
                existingUser.setContrasena(user.getContrasena());
                existingUser.setTelefonos(user.getTelefonos());
                existingUser.setEstaActivo(user.getEstaActivo());
                existingUser.setUltimoLogin(user.getUltimoLogin());
                existingUser.setToken(user.getToken());
                
                // Restaurar la fecha de creación original
                existingUser.setCreado(fechaCreacion);
                
                return userRepository.save(existingUser);
            })
            .orElse(null);
    }

    @Override
    @Transactional
    public User partialUpdateUser(UUID id, User user) {
        return userRepository.findById(id)
            .map(existingUser -> {
                if (user.getNombre() != null) existingUser.setNombre(user.getNombre());
                if (user.getCorreo() != null) existingUser.setCorreo(user.getCorreo());
                if (user.getContrasena() != null) existingUser.setContrasena(user.getContrasena());
                if (user.getTelefonos() != null) existingUser.setTelefonos(user.getTelefonos());
                if (user.getEstaActivo() != null) existingUser.setEstaActivo(user.getEstaActivo());
                return userRepository.save(existingUser);
            })
            .orElse(null);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(UUID id) {
        return userRepository.existsById(id);
    }
}
