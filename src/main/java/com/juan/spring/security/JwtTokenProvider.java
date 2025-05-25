package com.juan.spring.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwt-expiration-milliseconds}")
    private int jwtExpirationInMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generarToken(Authentication authentication) {
        String username = authentication.getName();
        Date fechaActual = new Date();

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(fechaActual)
                .signWith(key)
                .compact();
    }

    public String obtenerUsernameDelJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validarToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            // Firma JWT inválida
            return false;
        } catch (MalformedJwtException ex) {
            // Token JWT malformado
            return false;
        } catch (UnsupportedJwtException ex) {
            // Token JWT no soportado
            return false;
        } catch (IllegalArgumentException ex) {
            // Claims JWT vacío
            return false;
        }
    }
} 