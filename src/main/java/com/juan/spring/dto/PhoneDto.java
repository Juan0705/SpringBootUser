package com.juan.spring.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para teléfono de usuario")
public class PhoneDto {
    @Schema(hidden = true)
    private Long id;
    
    @Schema(description = "Número de teléfono", example = "1234567")
    private String numero;
    
    @Schema(description = "Código de ciudad", example = "1")
    private String codigoCiudad;
    
    @Schema(description = "Código de país", example = "57")
    private String codigoPais;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCodigoCiudad() {
        return codigoCiudad;
    }

    public void setCodigoCiudad(String codigoCiudad) {
        this.codigoCiudad = codigoCiudad;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }
} 