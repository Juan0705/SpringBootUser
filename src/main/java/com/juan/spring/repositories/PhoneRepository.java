package com.juan.spring.repositories;

import com.juan.spring.entities.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
    // Encontrar todos los teléfonos de un usuario específico
    List<Phone> findByUserId(UUID userId);
    
    // Encontrar teléfonos por código de país
    List<Phone> findByCodigoPais(String codigoPais);
    
    // Encontrar teléfonos por código de ciudad
    List<Phone> findByCodigoCiudad(String codigoCiudad);
    
    // Encontrar teléfonos por número
    List<Phone> findByNumero(String numero);
    
    // Eliminar todos los teléfonos de un usuario específico
    void deleteByUserId(UUID userId);
} 