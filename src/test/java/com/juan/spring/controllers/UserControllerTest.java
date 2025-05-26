package com.juan.spring.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juan.spring.dto.UserCreateUpdateDto;
import com.juan.spring.dto.UserDto;
import com.juan.spring.entities.User;
import com.juan.spring.security.JwtTokenProvider;
import com.juan.spring.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.argThat;

@WebMvcTest(UserController.class)
@Import(NoSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private UserCreateUpdateDto testUserDto;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        testUser = new User();
        testUser.setId(UUID.randomUUID());
        testUser.setNombre("Test User");
        testUser.setCorreo("test@test.com");
        testUser.setContrasena("Password1!");
        testUser.setEstaActivo(true);
        testUser.setCreado(LocalDateTime.now());
        testUser.setModificado(LocalDateTime.now());
        testUser.setUltimoLogin(LocalDateTime.now());
        testUser.setToken("test-token");

        // Crear DTO de prueba
        testUserDto = new UserCreateUpdateDto();
        testUserDto.setNombre("Test User");
        testUserDto.setCorreo("test@test.com");
        testUserDto.setContrasena("Password1!");
        testUserDto.setEstaActivo(true);

        // Configurar comportamiento por defecto del tokenProvider
        when(tokenProvider.generarToken(any())).thenReturn("test-token");

        when(userService.convertToDto(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setNombre(user.getNombre());
            dto.setCorreo(user.getCorreo());
            dto.setEstaActivo(user.getEstaActivo());
            return dto;
        });
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));

        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].nombre").value(testUser.getNombre()))
            .andExpect(jsonPath("$[0].correo").value(testUser.getCorreo()))
            .andExpect(jsonPath("$[0].estaActivo").value(testUser.getEstaActivo()));

        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        when(userService.getUserById(testUser.getId())).thenReturn(Optional.of(testUser));

        mockMvc.perform(get("/users/{id}", testUser.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value(testUser.getNombre()))
            .andExpect(jsonPath("$.correo").value(testUser.getCorreo()))
            .andExpect(jsonPath("$.estaActivo").value(testUser.getEstaActivo()));

        verify(userService).getUserById(testUser.getId());
    }

    @Test
    void createUser_WithValidData_ShouldReturnCreatedUser() throws Exception {
        UserDto userDtoValido = new UserDto();
        userDtoValido.setId(UUID.randomUUID());
        userDtoValido.setNombre(testUserDto.getNombre());
        userDtoValido.setCorreo(testUserDto.getCorreo());
        userDtoValido.setEstaActivo(testUserDto.getEstaActivo());
        when(userService.createUserWithValidation(any(UserCreateUpdateDto.class))).thenReturn(userDtoValido);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nombre").value(testUserDto.getNombre()))
            .andExpect(jsonPath("$.correo").value(testUserDto.getCorreo()))
            .andExpect(jsonPath("$.estaActivo").value(testUserDto.getEstaActivo()));
    }

    @Test
    void updateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        UserDto userDtoValido = new UserDto();
        userDtoValido.setId(testUser.getId());
        userDtoValido.setNombre(testUserDto.getNombre());
        userDtoValido.setCorreo(testUserDto.getCorreo());
        userDtoValido.setEstaActivo(testUserDto.getEstaActivo());
        when(userService.updateUserWithValidation(eq(testUser.getId()), any(UserCreateUpdateDto.class))).thenReturn(userDtoValido);
        mockMvc.perform(put("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value(testUserDto.getNombre()))
            .andExpect(jsonPath("$.correo").value(testUserDto.getCorreo()))
            .andExpect(jsonPath("$.estaActivo").value(testUserDto.getEstaActivo()));
    }

    @Test
    void partialUpdateUser_WithValidData_ShouldReturnUpdatedUser() throws Exception {
        UserDto userDtoValido = new UserDto();
        userDtoValido.setId(testUser.getId());
        userDtoValido.setNombre(testUserDto.getNombre());
        userDtoValido.setCorreo(testUserDto.getCorreo());
        userDtoValido.setEstaActivo(testUserDto.getEstaActivo());
        when(userService.partialUpdateUserWithValidation(eq(testUser.getId()), any(UserCreateUpdateDto.class))).thenReturn(userDtoValido);
        mockMvc.perform(patch("/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value(testUserDto.getNombre()))
            .andExpect(jsonPath("$.correo").value(testUserDto.getCorreo()))
            .andExpect(jsonPath("$.estaActivo").value(testUserDto.getEstaActivo()));
    }

    @Test
    void deleteUser_WhenUserExists_ShouldReturnNoContent() throws Exception {
        when(userService.existsById(testUser.getId())).thenReturn(true);
        doNothing().when(userService).deleteUser(testUser.getId());

        mockMvc.perform(delete("/users/{id}", testUser.getId()))
            .andExpect(status().isNoContent());

        verify(userService).deleteUser(testUser.getId());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(userService.getUserById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/{id}", nonExistentId))
            .andExpect(status().isNotFound());

        verify(userService).getUserById(nonExistentId);
    }

    @Test
    void updateUser_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(userService.updateUserWithValidation(eq(nonExistentId), any(UserCreateUpdateDto.class)))
            .thenThrow(new IllegalStateException("Usuario con ID " + nonExistentId + " no encontrado"));
        mockMvc.perform(put("/users/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void createUser_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        when(userService.createUserWithValidation(argThat(dto -> "invalid-email".equals(dto.getCorreo()))))
            .thenThrow(new IllegalArgumentException("El correo electrónico debe tener un formato válido (ejemplo: usuario@dominio.com)"));
        testUserDto.setCorreo("invalid-email");
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WithInvalidPassword_ShouldReturnBadRequest() throws Exception {
        when(userService.createUserWithValidation(argThat(dto -> "123".equals(dto.getContrasena()))))
            .thenThrow(new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"));
        testUserDto.setContrasena("123");
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WithExistingEmail_ShouldReturnConflict() throws Exception {
        when(userService.createUserWithValidation(any(UserCreateUpdateDto.class)))
            .thenThrow(new IllegalStateException("El correo test@test.com ya está registrado"));
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
            .andExpect(status().isConflict());
    }
} 