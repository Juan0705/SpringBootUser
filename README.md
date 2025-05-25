# API REST con Spring Boot y JWT

Este proyecto es una API REST desarrollada con Spring Boot que implementa autenticación JWT y OAuth2.

## Tecnologías Utilizadas

- Java 8
- Spring Boot 2.7.18
- Spring Security
- Spring Data JPA
- JWT (JSON Web Tokens)
- H2 Database
- Maven

## Requisitos Previos

- Java 8 o superior
- Maven
- Git

## Configuración del Proyecto

1. Clonar el repositorio:
```bash
git clone https://github.com/tu-usuario/nombre-del-repo.git
```

2. Navegar al directorio del proyecto:
```bash
cd nombre-del-repo
```

3. Compilar el proyecto:
```bash
mvn clean install
```

4. Ejecutar el proyecto:
```bash
mvn spring-boot:run
```

## Endpoints de la API

### Autenticación

#### Login
- **URL**: `/api/auth/login`
- **Método**: `POST`
- **Body**:
```json
{
    "email": "usuario@email.com",
    "password": "contraseña"
}
```

#### Registro
- **URL**: `/api/auth/registro`
- **Método**: `POST`
- **Body**:
```json
{
    "name": "Nombre Usuario",
    "email": "usuario@email.com",
    "password": "contraseña"
}
```

### Usuarios

#### Obtener Todos los Usuarios
- **URL**: `/users`
- **Método**: `GET`
- **Headers**: `Authorization: Bearer {token}`

#### Obtener Usuario por ID
- **URL**: `/users/{id}`
- **Método**: `GET`
- **Headers**: `Authorization: Bearer {token}`

## Base de Datos

El proyecto utiliza H2 Database en modo memoria. Puedes acceder a la consola H2 en:
- **URL**: `http://localhost:8000/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: (vacía)

## Seguridad

- La autenticación se realiza mediante JWT (JSON Web Tokens)
- Las contraseñas se almacenan encriptadas usando BCrypt
- Los endpoints de autenticación son públicos
- El resto de endpoints requieren autenticación mediante token JWT

## Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo `LICENSE` para más detalles. 