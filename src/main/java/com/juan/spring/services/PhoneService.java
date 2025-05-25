package com.juan.spring.services;

import com.juan.spring.entities.Phone;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PhoneService {
    // Obtener todos los teléfonos de un usuario
    List<Phone> getPhonesByUserId(UUID userId);
    
    // Obtener un teléfono específico
    Optional<Phone> getPhoneById(Long id);
    
    // Crear un nuevo teléfono
    Phone createPhone(Phone phone);
    
    // Actualizar un teléfono existente
    Phone updatePhone(Long id, Phone phoneDetails);
    
    // Actualizar parcialmente un teléfono
    Phone partialUpdatePhone(Long id, Phone phoneDetails);
    
    // Eliminar un teléfono
    void deletePhone(Long id);
    
    // Eliminar todos los teléfonos de un usuario
    void deletePhonesByUserId(UUID userId);
} 