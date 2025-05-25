package com.juan.spring.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "DTO para crear o actualizar un usuario")
public class UserCreateUpdateDto {
    @Schema(hidden = true)
    private UUID id;
    
    @Schema(description = "Nombre del usuario", example = "Juan S")
    private String nombre;
    
    @Schema(description = "Correo electrónico del usuario", example = "cualquiera@ejemplo.com")
    private String correo;
    
    @Schema(description = "Contraseña del usuario", example = "Juan!1sa")
    private String contrasena;
    
    @Schema(description = "Estado de activación del usuario", example = "true")
    private Boolean estaActivo;
    
    @Schema(hidden = true)
    private LocalDateTime creado;
    
    @Schema(hidden = true)
    private LocalDateTime modificado;
    
    @Schema(hidden = true)
    private LocalDateTime ultimoLogin;
    
    @Schema(hidden = true)
    private String token;
    
    @Schema(description = "Lista de teléfonos del usuario")
    private List<PhoneDto> telefonos;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public Boolean getEstaActivo() {
        return estaActivo;
    }

    public void setEstaActivo(Boolean estaActivo) {
        this.estaActivo = estaActivo;
    }

    public LocalDateTime getCreado() {
        return creado;
    }

    public void setCreado(LocalDateTime creado) {
        this.creado = creado;
    }

    public LocalDateTime getModificado() {
        return modificado;
    }

    public void setModificado(LocalDateTime modificado) {
        this.modificado = modificado;
    }

    public LocalDateTime getUltimoLogin() {
        return ultimoLogin;
    }

    public void setUltimoLogin(LocalDateTime ultimoLogin) {
        this.ultimoLogin = ultimoLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<PhoneDto> getTelefonos() {
        return telefonos;
    }

    public void setTelefonos(List<PhoneDto> telefonos) {
        this.telefonos = telefonos;
    }
} 