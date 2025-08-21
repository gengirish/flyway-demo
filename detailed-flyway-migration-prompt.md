## Task

Create a complete Spring Boot application with Flyway database migration integration in a single Java file with comprehensive test suite.

## Requirements

### Core Implementation

- **Single File Structure**: All classes as static inner classes in `FlywayDemoApplication.java`
- **Flyway Integration**: Automatic database migration on startup
- **Complete CRUD API**: REST endpoints for user management
- **Database**: H2 in-memory with proper JPA mapping
- **Testing**: Comprehensive test suite covering all components

### File Structure (4 files total)

```
src/main/java/com/example/flywaydemo/FlywayDemoApplication.java  # Single file with all classes
src/main/resources/application.properties                        # Configuration
src/main/resources/db/migration/V1__Create_user_table.sql       # Migration script
pom.xml                                                          # Maven dependencies
```

### User Entity Fields

- `id` (Primary Key, Auto-increment)
- `username` (VARCHAR, NOT NULL, UNIQUE)
- `email` (VARCHAR, NOT NULL, UNIQUE)
- `firstName`, `lastName` (VARCHAR)
- `createdAt`, `updatedAt` (TIMESTAMP with @PreUpdate)

### REST API Endpoints

- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user by ID
- `POST /api/users` - Create user
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/username/{username}` - Find by username
- `GET /api/users/email/{email}` - Find by email

### Dependencies (Maven)

```xml
spring-boot-starter-web
spring-boot-starter-data-jpa
flyway-core
h2
jackson-datatype-jsr310
spring-boot-starter-test
```

### Configuration Requirements

- H2 in-memory database with console enabled
- Flyway auto-migration enabled
- JPA with `hibernate.ddl-auto=validate`
- Proper logging configuration

### Test Suite Requirements

Create comprehensive test suite in single file `FlywayDemoApplicationTest.java`:

- **User Entity Tests**: Constructors, getters/setters, @PreUpdate functionality
- **User Service Tests**: All CRUD operations with Mockito mocking, exception handling
- **User Controller Tests**: REST API testing with MockMvc, HTTP status validation
- **Integration Tests**: Spring Boot context loading, end-to-end functionality

### Success Criteria

1. ✅ Single Java file with all classes as static inner classes
2. ✅ Flyway migration executes automatically on startup
3. ✅ All REST endpoints functional with proper HTTP status codes
4. ✅ Complete test coverage (29+ test scenarios)
5. ✅ H2 console accessible for database verification
6. ✅ Proper error handling and validation
7. ✅ JSON serialization with timestamp formatting

### Validation

- Application starts without errors
- Migration creates user table with constraints
- All CRUD operations work correctly
- Comprehensive test suite passes
- Database state persists correctly

**Provide the complete implementation with all 4 files ready to run.**
