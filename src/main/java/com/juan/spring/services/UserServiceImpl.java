package com.juan.spring.services;

import com.juan.spring.entities.User;
import com.juan.spring.entities.Phone;
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

    @Autowired
    private PhoneService phoneService;

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
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        // Actualizar campos básicos
        user.setNombre(userDetails.getNombre());
        user.setCorreo(userDetails.getCorreo());
        user.setEstaActivo(userDetails.getEstaActivo());
        user.setModificado(LocalDateTime.now());

        // Manejar teléfonos
        if (userDetails.getTelefonos() != null) {
            for (Phone phoneDetails : userDetails.getTelefonos()) {
                if (phoneDetails.getId() != null) {
                    // Verificar si el teléfono pertenece al usuario
                    Phone existingPhone = phoneService.getPhoneById(phoneDetails.getId())
                            .orElseThrow(() -> new RuntimeException("Teléfono no encontrado con id: " + phoneDetails.getId()));
                    
                    if (!existingPhone.getUser().getId().equals(id)) {
                        throw new RuntimeException("El teléfono no pertenece al usuario");
                    }
                    
                    // Actualizar teléfono existente
                    phoneService.updatePhone(phoneDetails.getId(), phoneDetails);
                } else {
                    // Crear nuevo teléfono
                    phoneDetails.setUser(user);
                    phoneService.createPhone(phoneDetails);
                }
            }
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User partialUpdateUser(UUID id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        if (userDetails.getNombre() != null) {
            user.setNombre(userDetails.getNombre());
        }
        if (userDetails.getCorreo() != null) {
            user.setCorreo(userDetails.getCorreo());
        }
        if (userDetails.getEstaActivo() != null) {
            user.setEstaActivo(userDetails.getEstaActivo());
        }
        user.setModificado(LocalDateTime.now());

        // Manejar teléfonos
        if (userDetails.getTelefonos() != null) {
            for (Phone phoneDetails : userDetails.getTelefonos()) {
                if (phoneDetails.getId() != null) {
                    // Verificar si el teléfono pertenece al usuario
                    Phone existingPhone = phoneService.getPhoneById(phoneDetails.getId())
                            .orElseThrow(() -> new RuntimeException("Teléfono no encontrado con id: " + phoneDetails.getId()));
                    
                    if (!existingPhone.getUser().getId().equals(id)) {
                        throw new RuntimeException("El teléfono no pertenece al usuario");
                    }
                    
                    // Actualizar teléfono existente
                    phoneService.updatePhone(phoneDetails.getId(), phoneDetails);
                } else {
                    // Crear nuevo teléfono
                    phoneDetails.setUser(user);
                    phoneService.createPhone(phoneDetails);
                }
            }
        }

        return userRepository.save(user);
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
