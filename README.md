# Experiencia-2-Backend-Fullstack-III
# Donaton BFF - API Gateway

Este repositorio contiene el Backend For Frontend (BFF) de la plataforma Donaton. Actúa como orquestador y punto de entrada único para las aplicaciones cliente.

## Arquitectura y Tecnologías
- **Lenguaje:** Java 21
- **Framework:** Spring Boot
- **Tolerancia a fallos:** Spring Cloud Circuit Breaker (Resilience4j)
- **Patrón:** Backend For Frontend (API Gateway)

## Funcionamiento
El BFF no se conecta a ninguna base de datos propia. Su función es recibir peticiones del Frontend, delegarlas a los microservicios de Donaciones (8081) y Logística (8082) vía `RestTemplate`, y agrupar la información. Si alguno de los servicios internos falla, el *Circuit Breaker* intercepta el error y devuelve un mensaje de contingencia.

## Configuración y Ejecución
1. Clonar el repositorio.
2. Asegurarse de que los microservicios internos estén corriendo en los puertos 8081 y 8082.
3. Ejecutar el servidor con:
   ```bash
   .\mvnw spring-boot:run
4. Alternativamente, ir a Spring Boot Dashboard y encenderlo desde ahi.
