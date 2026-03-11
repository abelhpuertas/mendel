# GEMINI.md - Mendel Java Code Challenge

## Project Overview
This project is an implementation of the Mendel Java Code Challenge. It provides a RESTful web service to store and manage transactions in memory, featuring transitive sum calculations across transaction hierarchies.

- **Main Technologies:** Java 21, Spring Boot 3.4.3, Maven, Lombok, MapStruct.
- **Architecture:** Multi-module layered architecture (API → Business → Model).
- **Storage:** In-memory storage using `HashMap` and custom indexing (no SQL).

### Module Structure
- `mendel-api`: REST Controllers and global exception handling.
- `mendel-business`: Core business logic, services, and MapStruct mappers.
- `mendel-dto`: Data Transfer Objects for API requests/responses.
- `mendel-model`: Domain entities (`{Domain}Entity`) and in-memory repositories.
- `mendel-util`: Common utilities and Enums (prefixed with `E`).

## Building and Running
The project uses the Maven Wrapper (`mvnw`).

- **Build:** `./mvnw clean package`
- **Run Application:** `./mvnw spring-boot:run -pl mendel-api`
- **Run Tests:** `./mvnw test`
- **Docker Build:** `docker build -t mendel-challenge .`
- **Docker Run:** `docker run -p 8080:8080 mendel-challenge`

## Development Conventions

### Coding Standards
- **Java Version:** Always use **Java 21** features.
- **Naming:** 
  - Classes: `PascalCase`.
  - Variables/Methods: `camelCase`.
  - Enums: Prefix with `E` (e.g., `EMendelExceptionCode`).
  - Entities: Suffix with `Entity` (e.g., `TransactionEntity`).
- **Lombok Usage:**
  - Use `@Data`, `@Builder`, `@NoArgsConstructor`, and `@AllArgsConstructor` for DTOs.
  - Use `@Getter` and `@Setter` separately for Entities.
  - Use `@RequiredArgsConstructor` for constructor-based dependency injection.
  - Use `@Slf4j` for logging.
- **Null Safety:** Use `Objects.isNull()` and `Objects.nonNull()` instead of direct `== null` or `!= null` checks.
- **String Handling:** Use Apache Commons Lang `StringUtils` for validations (e.g., `StringUtils.isBlank()`).
- **Monetary Values:** **ALWAYS** use `BigDecimal` for amounts to ensure precision.
- **Variables:** **NEVER** use the `var` keyword.

### API & Data Layer
- **JSON Mapping:** API responses must use `snake_case` (use `@JsonProperty` in DTOs).
- **Thin Controllers:** Controllers must only handle input validation and delegation to services. They must **always** use DTOs, never entities.
- **Service Layer:** Services handle business logic and mapping. They must never expose entities in public methods.
- **Mappers:** Use **MapStruct** for all entity-to-DTO conversions following the `INSTANCE` pattern.

### Error Handling
- Use `MendelException` along with `EMendelExceptionCode` for business-level errors.
- Global exception handling is managed in `GlobalExceptionHandler` within the `mendel-api` module.

### Testing Practices
- **Frameworks:** JUnit 5 and Mockito.
- **Method Naming:** `should{ExpectedBehavior}_when{Condition}` (e.g., `shouldReturnSum_whenTransactionExists`).
- **Requirements:** Every new feature must include unit tests. Integration tests should be placed in the `mendel-api` module.

## Key Files
- `AGENTS.md`: Detailed coding standards and Copilot instructions.
