package com.juan.spring.services;

import com.juan.spring.entities.User;
import com.juan.spring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public User updateUser(UUID id, User userDetails) {
        return userRepository.findById(id)
            .map(existingUser -> {
                // Preservar la fecha de creación original
                LocalDateTime fechaCreacion = existingUser.getCreado();
                
                // Actualizar campos básicos
                existingUser.setNombre(userDetails.getNombre());
                existingUser.setCorreo(userDetails.getCorreo());
                
                // Actualizar contraseña solo si se proporciona una nueva
                if (userDetails.getContrasena() != null && !userDetails.getContrasena().isEmpty()) {
                    existingUser.setContrasena(passwordEncoder.encode(userDetails.getContrasena()));
                }
                
                existingUser.setEstaActivo(userDetails.getEstaActivo());
                existingUser.setUltimoLogin(userDetails.getUltimoLogin());
                existingUser.setToken(userDetails.getToken());
                
                // Manejar la colección de teléfonos
                if (userDetails.getTelefonos() != null) {
                    // Limpiar la colección existente
                    existingUser.getTelefonos().clear();
                    // Agregar los nuevos teléfonos
                    userDetails.getTelefonos().forEach(phone -> {
                        phone.setUser(existingUser);
                        existingUser.getTelefonos().add(phone);
                    });
                }
                
                // Restaurar la fecha de creación original
                existingUser.setCreado(fechaCreacion);
                
                return userRepository.save(existingUser);
            })
            .orElse(null);
    }

    @Override
    @Transactional
    public User partialUpdateUser(UUID id, User userDetails) {
        return userRepository.findById(id)
            .map(existingUser -> {
                if (userDetails.getNombre() != null) {
                    existingUser.setNombre(userDetails.getNombre());
                }
                if (userDetails.getCorreo() != null) {
                    existingUser.setCorreo(userDetails.getCorreo());
                }
                if (userDetails.getContrasena() != null && !userDetails.getContrasena().isEmpty()) {
                    existingUser.setContrasena(passwordEncoder.encode(userDetails.getContrasena()));
                }
                if (userDetails.getEstaActivo() != null) {
                    existingUser.setEstaActivo(userDetails.getEstaActivo());
                }
                if (userDetails.getTelefonos() != null) {
                    existingUser.getTelefonos().clear();
                    userDetails.getTelefonos().forEach(phone -> {
                        phone.setUser(existingUser);
                        existingUser.getTelefonos().add(phone);
                    });
                }
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
