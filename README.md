# Mendel Java Code Challenge - Solución

Esta es una implementación del Mendel Java Code Challenge utilizando Spring Boot 3.4.3, Java 21 y Maven. Proporciona un servicio RESTful para almacenar y gestionar transacciones en memoria, incluyendo cálculos de sumas transitivas.

## Arquitectura
El proyecto está estructurado utilizando una **Arquitectura en Capas Multi-módulo** para garantizar una fuerte separación de responsabilidades:
- **`mendel-api`**: Controladores REST, manejo global de excepciones y configuración de la aplicación.
- **`mendel-business`**: Lógica de negocio core, servicios y mapeadores (MapStruct).
- **`mendel-dto`**: Objetos de Transferencia de Datos (DTOs) para las peticiones y respuestas de la API.
- **`mendel-model`**: Entidades de dominio y repositorios en memoria.
- **`mendel-util`**: Utilidades comunes y enumeraciones compartidas.

## Decisiones Técnicas Clave
- **Almacenamiento In-Memory**: Se optó por una estructura  `HashMap` para el almacenamiento sin base de datos SQL.
- **Precisión Monetaria**: Se utiliza estrictamente `BigDecimal` para todos los montos para evitar problemas de precisión de punto flotante.
- **Mapeo de Datos**: Uso de **MapStruct** para las conversiones entre Entidades y DTOs, asegurando que los Controladores sean "delgados" y nunca expongan el modelo de dominio.
- **Cálculo de Suma Transitiva**: El cálculo se realiza navegando por la jerarquía de `parent_id` de las transacciones.
- **Lombok**: Utilizado extensamente para reducir el boilerplate (builders, data classes, loggers).

## Requisitos
- Java 21
- Maven (usando el Wrapper incluido)

## Cómo ejecutar localmente
1. Clonar el repositorio.
2. Compilar el proyecto y ejecutar los tests:
   ```bash
   ./mvnw clean package
   ```
3. Iniciar la aplicación (especificando el módulo API):
   ```bash
   ./mvnw spring-boot:run -pl mendel-api
   ```

## Cómo ejecutar con Docker
1. Construir la imagen:
   ```bash
   docker build -t mendel-challenge .
   ```
2. Ejecutar el contenedor:
   ```bash
   docker run -p 8080:8080 mendel-challenge
   ```

## Endpoints Principales
- `PUT /transactions/{transaction_id}`: Guarda una nueva transacción.
- `GET /transactions/types/{type}`: Devuelve una lista de IDs de transacciones por tipo.
- `GET /transactions/sum/{transaction_id}`: Devuelve la suma transitiva de los montos de una transacción y todos sus hijos.
