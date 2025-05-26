package com.juan.spring.services;

import com.juan.spring.entities.User;
import com.juan.spring.entities.Phone;
import com.juan.spring.repositories.UserRepository;
import com.juan.spring.dto.UserDto;
import com.juan.spring.dto.UserCreateUpdateDto;
import com.juan.spring.dto.ValidationErrorResponse;
import com.juan.spring.dto.PhoneDto;
import com.juan.spring.validation.ValidationPatterns;
import com.juan.spring.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PhoneService phoneService;

    @Autowired
    private JwtTokenProvider tokenProvider;

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

    @Override
    public ValidationErrorResponse validateUserData(UserCreateUpdateDto userDto) {
        ValidationErrorResponse validationErrors = new ValidationErrorResponse();

        if (userDto.getCorreo() != null && !isValidEmail(userDto.getCorreo())) {
            validationErrors.addError(ValidationPatterns.EMAIL_ERROR_MESSAGE);
        }

        if (userDto.getContrasena() != null && !isValidPassword(userDto.getContrasena())) {
            validationErrors.addError(ValidationPatterns.PASSWORD_ERROR_MESSAGE);
        }

        return validationErrors;
    }

    private boolean isValidEmail(String email) {
        return Pattern.matches(ValidationPatterns.EMAIL_PATTERN, email);
    }

    private boolean isValidPassword(String password) {
        return Pattern.matches(ValidationPatterns.PASSWORD_PATTERN, password);
    }

    @Override
    public UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setNombre(user.getNombre());
        dto.setCorreo(user.getCorreo());
        dto.setEstaActivo(user.getEstaActivo());
        dto.setCreado(user.getCreado());
        dto.setModificado(user.getModificado());
        dto.setUltimoLogin(user.getUltimoLogin());
        dto.setToken(user.getToken());
        
        if (user.getTelefonos() != null) {
            List<PhoneDto> phoneDtos = user.getTelefonos().stream()
                .map(phone -> {
                    PhoneDto phoneDto = new PhoneDto();
                    phoneDto.setId(phone.getId());
                    phoneDto.setNumero(phone.getNumero());
                    phoneDto.setCodigoCiudad(phone.getCodigoCiudad());
                    phoneDto.setCodigoPais(phone.getCodigoPais());
                    return phoneDto;
                })
                .collect(Collectors.toList());
            dto.setTelefonos(phoneDtos);
        }
        
        return dto;
    }

    @Override
    public User convertToEntity(UserCreateUpdateDto dto) {
        User user = new User();
        if (dto.getId() != null) {
            user.setId(dto.getId());
        }
        if (dto.getNombre() != null) {
            user.setNombre(dto.getNombre());
        }
        if (dto.getCorreo() != null) {
            user.setCorreo(dto.getCorreo());
        }
        if (dto.getContrasena() != null && !dto.getContrasena().isEmpty()) {
            user.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        if (dto.getEstaActivo() != null) {
            user.setEstaActivo(dto.getEstaActivo());
        }
        if (dto.getCreado() != null) {
            user.setCreado(dto.getCreado());
        }
        if (dto.getModificado() != null) {
            user.setModificado(dto.getModificado());
        }
        if (dto.getUltimoLogin() != null) {
            user.setUltimoLogin(dto.getUltimoLogin());
        }
        if (dto.getToken() != null) {
            user.setToken(dto.getToken());
        }
        
        if (dto.getTelefonos() != null) {
            List<Phone> phones = dto.getTelefonos().stream()
                .map(phoneDto -> {
                    Phone phone = new Phone();
                    if (phoneDto.getId() != null) {
                        phone.setId(phoneDto.getId());
                    }
                    phone.setNumero(phoneDto.getNumero());
                    phone.setCodigoCiudad(phoneDto.getCodigoCiudad());
                    phone.setCodigoPais(phoneDto.getCodigoPais());
                    phone.setUser(user);
                    return phone;
                })
                .collect(Collectors.toList());
            user.setTelefonos(phones);
        }
        
        return user;
    }

    @Override
    @Transactional
    public UserDto createUserWithValidation(UserCreateUpdateDto userDto) {
        ValidationErrorResponse validationErrors = validateUserData(userDto);
        if (!validationErrors.getErrors().isEmpty()) {
            throw new IllegalArgumentException(validationErrors.getErrors().get(0));
        }

        if (userDto.getCorreo() == null || userDto.getCorreo().trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.EMAIL_ERROR_MESSAGE);
        }

        if (userDto.getContrasena() == null || userDto.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.PASSWORD_ERROR_MESSAGE);
        }

        if (!isEmailAvailable(userDto.getCorreo(), null)) {
            throw new IllegalStateException("El correo " + userDto.getCorreo() + " ya está registrado");
        }

        User user = convertToEntity(userDto);
        LocalDateTime now = LocalDateTime.now();
        
        user.setEstaActivo(true);
        user.setUltimoLogin(now);
        user.setCreado(now);

        // Generar token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            user.getCorreo(),
            user.getContrasena()
        );
        String jwt = tokenProvider.generarToken(authentication);
        user.setToken(jwt);

        User createdUser = createUser(user);
        return convertToDto(createdUser);
    }

    @Override
    @Transactional
    public UserDto updateUserWithValidation(UUID id, UserCreateUpdateDto userDto) {
        if (!existsById(id)) {
            throw new IllegalStateException("Usuario con ID " + id + " no encontrado");
        }

        ValidationErrorResponse validationErrors = validateUserData(userDto);
        if (!validationErrors.getErrors().isEmpty()) {
            throw new IllegalArgumentException(validationErrors.getErrors().get(0));
        }

        if (userDto.getCorreo() == null || userDto.getCorreo().trim().isEmpty()) {
            throw new IllegalArgumentException(ValidationPatterns.EMAIL_ERROR_MESSAGE);
        }

        if (!isEmailAvailable(userDto.getCorreo(), id)) {
            throw new IllegalStateException("El correo " + userDto.getCorreo() + " ya está registrado para otro usuario");
        }

        User user = convertToEntity(userDto);
        user.setModificado(LocalDateTime.now());
        User updatedUser = updateUser(id, user);
        
        if (updatedUser == null) {
            throw new IllegalStateException("Error al actualizar el usuario");
        }
        
        return convertToDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto partialUpdateUserWithValidation(UUID id, UserCreateUpdateDto userDto) {
        if (!existsById(id)) {
            throw new IllegalStateException("Usuario con ID " + id + " no encontrado");
        }

        if (userDto.getCorreo() != null) {
            if (!isValidEmail(userDto.getCorreo())) {
                throw new IllegalArgumentException(ValidationPatterns.EMAIL_ERROR_MESSAGE);
            }
            if (!isEmailAvailable(userDto.getCorreo(), id)) {
                throw new IllegalStateException("El correo " + userDto.getCorreo() + " ya está registrado para otro usuario");
            }
        }

        if (userDto.getContrasena() != null && !isValidPassword(userDto.getContrasena())) {
            throw new IllegalArgumentException(ValidationPatterns.PASSWORD_ERROR_MESSAGE);
        }

        User user = convertToEntity(userDto);
        user.setModificado(LocalDateTime.now());
        User updatedUser = partialUpdateUser(id, user);
        
        if (updatedUser == null) {
            throw new IllegalStateException("Error al actualizar el usuario");
        }
        
        return convertToDto(updatedUser);
    }

    @Override
    public boolean isEmailAvailable(String email, UUID excludeUserId) {
        Optional<User> existingUser = getUserByEmail(email);
        return !existingUser.isPresent() || 
               (excludeUserId != null && existingUser.get().getId().equals(excludeUserId));
    }
}
