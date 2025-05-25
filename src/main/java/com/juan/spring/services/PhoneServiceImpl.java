package com.juan.spring.services;

import com.juan.spring.entities.Phone;
import com.juan.spring.entities.User;
import com.juan.spring.repositories.PhoneRepository;
import com.juan.spring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PhoneServiceImpl implements PhoneService {

    @Autowired
    private PhoneRepository phoneRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Phone> getPhonesByUserId(UUID userId) {
        return phoneRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Phone> getPhoneById(Long id) {
        return phoneRepository.findById(id);
    }

    @Override
    @Transactional
    public Phone createPhone(Phone phone) {
        return phoneRepository.save(phone);
    }

    @Override
    @Transactional
    public Phone updatePhone(Long id, Phone phoneDetails) {
        Phone phone = phoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teléfono no encontrado con id: " + id));

        phone.setNumero(phoneDetails.getNumero());
        phone.setCodigoCiudad(phoneDetails.getCodigoCiudad());
        phone.setCodigoPais(phoneDetails.getCodigoPais());

        return phoneRepository.save(phone);
    }

    @Override
    @Transactional
    public Phone partialUpdatePhone(Long id, Phone phoneDetails) {
        Phone phone = phoneRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teléfono no encontrado con id: " + id));

        if (phoneDetails.getNumero() != null) {
            phone.setNumero(phoneDetails.getNumero());
        }
        if (phoneDetails.getCodigoCiudad() != null) {
            phone.setCodigoCiudad(phoneDetails.getCodigoCiudad());
        }
        if (phoneDetails.getCodigoPais() != null) {
            phone.setCodigoPais(phoneDetails.getCodigoPais());
        }

        return phoneRepository.save(phone);
    }

    @Override
    @Transactional
    public void deletePhone(Long id) {
        phoneRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deletePhonesByUserId(UUID userId) {
        phoneRepository.deleteByUserId(userId);
    }
} 