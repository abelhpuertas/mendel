# Copilot Instructions - Mendel Challenge

## Project Context

- Java 21 Spring Boot microservice for transaction operations
- Multi-module Maven project structure
- Parent POM: `com.mendel:mendel-parent`
- Modules: mendel-api, mendel-business, mendel-dto, mendel-model, mendel-util
- Base package: `com.mendel`
- Data layer: In-memory storage (as per challenge requirements)

## Architecture & Module Structure

```
mendel-api/       - REST controllers, configuration, filters
mendel-business/  - Business logic, services, mappers, exception handling
mendel-model/     - Entities, repositories
mendel-dto/       - Data transfer objects for API
mendel-util/      - Enums (E-prefix convention), constants, helpers
```

**Module Dependency Direction:**
```
mendel-api → mendel-business → mendel-model → mendel-dto → mendel-util
```

## Code Standards

### Lombok Usage

- **Use `@Data` annotation for DTOs**
- **Do NOT manually declare getter/setter methods** when using Lombok
- For entities, prefer `@Getter` and `@Setter` separately
- Use `@Builder` for complex object construction
- Use `@NoArgsConstructor` and `@AllArgsConstructor` when needed
- Use `@RequiredArgsConstructor` for constructor injection in services
- Use `@Slf4j` for logging

### General Java

- Use Java 21 features
- Follow camelCase for variables/methods, PascalCase for classes
- Use meaningful and descriptive names
- Prefer immutable objects when possible
- Use Optional instead of null checks
- **Use `Objects.isNull()` and `Objects.nonNull()` instead of `== null` and `!= null`**
- Implement proper equals(), hashCode(), and toString() (or use Lombok)
- **Do NOT create nested classes within DTOs or entities**
- **Avoid magic numbers and hardcoded values**
- **NEVER use `var` keyword for variable declarations**
- **Use `BigDecimal` for all monetary/amount values** to ensure precision.

### String Handling

- **Use Apache Commons Lang StringUtils for all String validation**
- **Use `StringUtils.isBlank()` to check for null, empty, or whitespace-only strings**

### Spring Boot Specific

- Use constructor injection over field injection (use `@RequiredArgsConstructor`)
- Annotate services with @Service, repositories with @Repository
- Use @RestController for REST endpoints
- Implement proper exception handling with @ControllerAdvice

### Controller Guidelines

- **Controllers MUST be thin** - No business logic in controllers
- **Controllers MUST ALWAYS use DTOs for requests and responses**
- Controllers should only:
    - Validate input parameters
    - Call appropriate service methods
    - Return HTTP responses

### Module Guidelines

- **mendel-api**: Controllers, configuration, security
- **mendel-business**: Service classes, business logic, validation, mappers, exception classes
- **mendel-dto**: Request/Response DTOs. **JSON properties MUST use snake_case**.
- **mendel-model**: Entities (using `{Domain}Entity` suffix), repositories
- **mendel-util**: Enums (E-prefix convention), constants, helpers

### Service Layer Guidelines

- **Services MUST NOT expose entities in public methods** - Always use DTOs
- Public service methods should only accept and return DTOs from mendel-dto module
- Entities should only be used internally within services
- Use mappers to convert between entities and DTOs

### Mapper Guidelines

- **Use MapStruct for all entity-to-DTO conversions**
- Mappers MUST be interfaces annotated with `@Mapper`
- Use the INSTANCE pattern

### Error Handling

- Use `MendelException` with `EMendelExceptionCode` for all business logic errors
- Add new error codes to `EMendelExceptionCode` enum when needed

### Testing Requirements

- **Every new feature MUST include unit tests**
- Use **JUnit 5 and Mockito** for unit tests
- Test method naming convention: `should{ExpectedBehavior}_when{Condition}`
- Use `@ExtendWith(MockitoExtension.class)` for test classes
- Mock external dependencies (repositories, other services)
