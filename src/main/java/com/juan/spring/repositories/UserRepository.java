package com.juan.spring.repositories;

import org.springframework.data.repository.CrudRepository;

import com.juan.spring.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByCorreo(String correo);
}
