package com.juan.spring.config;

import com.juan.spring.entities.User;
import com.juan.spring.entities.Phone;
import com.juan.spring.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear usuarios de ejemplo
        User user1 = new User();
        user1.setNombre("Juan Pérez");
        user1.setCorreo("juan@email.com");
        user1.setContrasena("123456");
        user1.setCreado(LocalDateTime.now());
        user1.setEstaActivo(true);
        user1.setTelefonos(Arrays.asList(
            createPhone("123456789", "1", "57"),
            createPhone("987654321", "2", "57")
        ));

        User user2 = new User();
        user2.setNombre("María García");
        user2.setCorreo("maria@email.com");
        user2.setContrasena("654321");
        user2.setCreado(LocalDateTime.now());
        user2.setEstaActivo(true);
        user2.setTelefonos(Arrays.asList(
            createPhone("555555555", "1", "57")
        ));

        User user3 = new User();
        user3.setNombre("Carlos López");
        user3.setCorreo("carlos@email.com");
        user3.setContrasena("qwerty");
        user3.setCreado(LocalDateTime.now());
        user3.setEstaActivo(true);

        User user4 = new User();
        user4.setNombre("Ana Martínez");
        user4.setCorreo("ana@email.com");
        user4.setContrasena("asdfgh");
        user4.setCreado(LocalDateTime.now());
        user4.setEstaActivo(false);
        user4.setTelefonos(Arrays.asList(
            createPhone("111111111", "1", "57"),
            createPhone("222222222", "2", "57"),
            createPhone("333333333", "3", "57")
        ));

        User user5 = new User();
        user5.setNombre("Pedro Sánchez");
        user5.setCorreo("pedro@email.com");
        user5.setContrasena("zxcvbn");
        user5.setCreado(LocalDateTime.now());
        user5.setEstaActivo(true);
        user5.setTelefonos(Arrays.asList(
            createPhone("444444444", "1", "57")
        ));

        // Guardar usuarios en la base de datos
        userRepository.saveAll(Arrays.asList(user1, user2, user3, user4, user5));
    }

    private Phone createPhone(String numero, String codigoCiudad, String codigoPais) {
        Phone phone = new Phone();
        phone.setNumero(numero);
        phone.setCodigoCiudad(codigoCiudad);
        phone.setCodigoPais(codigoPais);
        return phone;
    }
} 