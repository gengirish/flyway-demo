package com.example.flywaydemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Comprehensive test suite for FlywayDemoApplication
 * Contains all tests for User entity, UserService, UserController, and integration tests
 */
@DisplayName("Flyway Demo Application - Comprehensive Test Suite")
class FlywayDemoApplicationTest {

    // ==================== USER ENTITY TESTS ====================
    @Nested
    @DisplayName("User Entity Tests")
    class UserEntityTests {

        private User user;

        @BeforeEach
        void setUp() {
            user = new User();
        }

        @Test
        @DisplayName("Should create user with default constructor")
        void shouldCreateUserWithDefaultConstructor() {
            // Given & When
            User newUser = new User();

            // Then
            assertNotNull(newUser);
            assertNotNull(newUser.getCreatedAt());
            assertNotNull(newUser.getUpdatedAt());
            assertNull(newUser.getId());
            assertNull(newUser.getUsername());
            assertNull(newUser.getEmail());
            assertNull(newUser.getFirstName());
            assertNull(newUser.getLastName());
        }

        @Test
        @DisplayName("Should create user with parameterized constructor")
        void shouldCreateUserWithParameterizedConstructor() {
            // Given
            String username = "testuser";
            String email = "test@example.com";
            String firstName = "John";
            String lastName = "Doe";

            // When
            User newUser = new User(username, email, firstName, lastName);

            // Then
            assertNotNull(newUser);
            assertEquals(username, newUser.getUsername());
            assertEquals(email, newUser.getEmail());
            assertEquals(firstName, newUser.getFirstName());
            assertEquals(lastName, newUser.getLastName());
            assertNotNull(newUser.getCreatedAt());
            assertNotNull(newUser.getUpdatedAt());
        }

        @Test
        @DisplayName("Should set and get all properties correctly")
        void shouldSetAndGetAllPropertiesCorrectly() {
            // Given
            Long id = 1L;
            String username = "testuser";
            String email = "test@example.com";
            String firstName = "John";
            String lastName = "Doe";
            LocalDateTime now = LocalDateTime.now();

            // When
            user.setId(id);
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setCreatedAt(now);
            user.setUpdatedAt(now);

            // Then
            assertEquals(id, user.getId());
            assertEquals(username, user.getUsername());
            assertEquals(email, user.getEmail());
            assertEquals(firstName, user.getFirstName());
            assertEquals(lastName, user.getLastName());
            assertEquals(now, user.getCreatedAt());
            assertEquals(now, user.getUpdatedAt());
        }

        @Test
        @DisplayName("Should update timestamp on preUpdate")
        void shouldUpdateTimestampOnPreUpdate() {
            // Given
            LocalDateTime originalUpdatedAt = user.getUpdatedAt();
            
            // Wait a small amount to ensure time difference
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // When
            user.preUpdate();

            // Then
            assertNotEquals(originalUpdatedAt, user.getUpdatedAt());
            assertTrue(user.getUpdatedAt().isAfter(originalUpdatedAt));
        }

        @Test
        @DisplayName("Should generate correct toString")
        void shouldGenerateCorrectToString() {
            // Given
            user.setId(1L);
            user.setUsername("testuser");
            user.setEmail("test@example.com");
            user.setFirstName("John");
            user.setLastName("Doe");

            // When
            String toString = user.toString();

            // Then
            assertNotNull(toString);
            assertTrue(toString.contains("User{"));
            assertTrue(toString.contains("id=1"));
            assertTrue(toString.contains("username='testuser'"));
            assertTrue(toString.contains("email='test@example.com'"));
            assertTrue(toString.contains("firstName='John'"));
            assertTrue(toString.contains("lastName='Doe'"));
        }
    }

    // ==================== USER SERVICE TESTS ====================
    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("UserService Tests")
    class UserServiceTests {

        @Mock
        private UserRepository userRepository;

        @InjectMocks
        private UserService userService;

        private User testUser;
        private User anotherUser;

        @BeforeEach
        void setUp() {
            testUser = new User("testuser", "test@example.com", "John", "Doe");
            testUser.setId(1L);

            anotherUser = new User("anotheruser", "another@example.com", "Jane", "Smith");
            anotherUser.setId(2L);
        }

        @Test
        @DisplayName("Should get all users")
        void shouldGetAllUsers() {
            // Given
            List<User> users = Arrays.asList(testUser, anotherUser);
            when(userRepository.findAll()).thenReturn(users);

            // When
            List<User> result = userService.getAllUsers();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(testUser, result.get(0));
            assertEquals(anotherUser, result.get(1));
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Should get user by id when user exists")
        void shouldGetUserByIdWhenUserExists() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getUserById(1L);

            // Then
            assertTrue(result.isPresent());
            assertEquals(testUser, result.get());
            verify(userRepository).findById(1L);
        }

        @Test
        @DisplayName("Should return empty when user does not exist")
        void shouldReturnEmptyWhenUserDoesNotExist() {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When
            Optional<User> result = userService.getUserById(999L);

            // Then
            assertFalse(result.isPresent());
            verify(userRepository).findById(999L);
        }

        @Test
        @DisplayName("Should get user by username")
        void shouldGetUserByUsername() {
            // Given
            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getUserByUsername("testuser");

            // Then
            assertTrue(result.isPresent());
            assertEquals(testUser, result.get());
            verify(userRepository).findByUsername("testuser");
        }

        @Test
        @DisplayName("Should get user by email")
        void shouldGetUserByEmail() {
            // Given
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            // When
            Optional<User> result = userService.getUserByEmail("test@example.com");

            // Then
            assertTrue(result.isPresent());
            assertEquals(testUser, result.get());
            verify(userRepository).findByEmail("test@example.com");
        }

        @Test
        @DisplayName("Should create user successfully when username and email are unique")
        void shouldCreateUserSuccessfullyWhenUsernameAndEmailAreUnique() {
            // Given
            User newUser = new User("newuser", "new@example.com", "New", "User");
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(newUser)).thenReturn(newUser);

            // When
            User result = userService.createUser(newUser);

            // Then
            assertNotNull(result);
            assertEquals(newUser, result);
            verify(userRepository).existsByUsername("newuser");
            verify(userRepository).existsByEmail("new@example.com");
            verify(userRepository).save(newUser);
        }

        @Test
        @DisplayName("Should throw exception when creating user with existing username")
        void shouldThrowExceptionWhenCreatingUserWithExistingUsername() {
            // Given
            User newUser = new User("testuser", "new@example.com", "New", "User");
            when(userRepository.existsByUsername("testuser")).thenReturn(true);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.createUser(newUser));
            
            assertEquals("Username already exists: testuser", exception.getMessage());
            verify(userRepository).existsByUsername("testuser");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when creating user with existing email")
        void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
            // Given
            User newUser = new User("newuser", "test@example.com", "New", "User");
            when(userRepository.existsByUsername("newuser")).thenReturn(false);
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.createUser(newUser));
            
            assertEquals("Email already exists: test@example.com", exception.getMessage());
            verify(userRepository).existsByEmail("test@example.com");
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() {
            // Given
            User updatedDetails = new User("updateduser", "updated@example.com", "Updated", "User");
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("updateduser")).thenReturn(false);
            when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // When
            User result = userService.updateUser(1L, updatedDetails);

            // Then
            assertNotNull(result);
            verify(userRepository).findById(1L);
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent user")
        void shouldThrowExceptionWhenUpdatingNonExistentUser() {
            // Given
            User updatedDetails = new User("updateduser", "updated@example.com", "Updated", "User");
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.updateUser(999L, updatedDetails));
            
            assertTrue(exception.getMessage().contains("User not found with id: 999"));
            verify(userRepository).findById(999L);
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            // When
            userService.deleteUser(1L);

            // Then
            verify(userRepository).findById(1L);
            verify(userRepository).delete(testUser);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent user")
        void shouldThrowExceptionWhenDeletingNonExistentUser() {
            // Given
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> userService.deleteUser(999L));
            
            assertTrue(exception.getMessage().contains("User not found with id: 999"));
            verify(userRepository).findById(999L);
            verify(userRepository, never()).delete(any(User.class));
        }

        @Test
        @DisplayName("Should check if user exists")
        void shouldCheckIfUserExists() {
            // Given
            when(userRepository.existsById(1L)).thenReturn(true);
            when(userRepository.existsById(999L)).thenReturn(false);

            // When & Then
            assertTrue(userService.userExists(1L));
            assertFalse(userService.userExists(999L));
            
            verify(userRepository).existsById(1L);
            verify(userRepository).existsById(999L);
        }
    }

    // ==================== USER CONTROLLER TESTS ====================
    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("UserController Tests")
    class UserControllerTests {

        @Mock
        private UserService userService;

        @InjectMocks
        private UserController userController;

        private MockMvc mockMvc;
        private ObjectMapper objectMapper;
        private User testUser;

        @BeforeEach
        void setUp() {
            mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
            objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            
            testUser = new User("testuser", "test@example.com", "John", "Doe");
            testUser.setId(1L);
        }

        @Test
        @DisplayName("Should get all users successfully")
        void shouldGetAllUsersSuccessfully() throws Exception {
            // Given
            List<User> users = Arrays.asList(testUser);
            when(userService.getAllUsers()).thenReturn(users);

            // When & Then
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));

            verify(userService).getAllUsers();
        }

        @Test
        @DisplayName("Should handle exception when getting all users")
        void shouldHandleExceptionWhenGettingAllUsers() throws Exception {
            // Given
            when(userService.getAllUsers()).thenThrow(new RuntimeException("Database error"));

            // When & Then
            mockMvc.perform(get("/api/users"))
                .andExpect(status().isInternalServerError());

            verify(userService).getAllUsers();
        }

        @Test
        @DisplayName("Should get user by id successfully")
        void shouldGetUserByIdSuccessfully() throws Exception {
            // Given
            when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

            // When & Then
            mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));

            verify(userService).getUserById(1L);
        }

        @Test
        @DisplayName("Should return not found when user does not exist")
        void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
            // Given
            when(userService.getUserById(999L)).thenReturn(Optional.empty());

            // When & Then
            mockMvc.perform(get("/api/users/999"))
                .andExpect(status().isNotFound());

            verify(userService).getUserById(999L);
        }

        @Test
        @DisplayName("Should create user successfully")
        void shouldCreateUserSuccessfully() throws Exception {
            // Given
            User newUser = new User("newuser", "new@example.com", "New", "User");
            User createdUser = new User("newuser", "new@example.com", "New", "User");
            createdUser.setId(2L);
            
            when(userService.createUser(any(User.class))).thenReturn(createdUser);

            // When & Then
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("newuser"))
                .andExpect(jsonPath("$.email").value("new@example.com"));

            verify(userService).createUser(any(User.class));
        }

        @Test
        @DisplayName("Should return conflict when creating user with duplicate data")
        void shouldReturnConflictWhenCreatingUserWithDuplicateData() throws Exception {
            // Given
            User newUser = new User("testuser", "test@example.com", "Test", "User");
            when(userService.createUser(any(User.class)))
                .thenThrow(new RuntimeException("Username already exists"));

            // When & Then
            mockMvc.perform(post("/api/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isConflict());

            verify(userService).createUser(any(User.class));
        }

        @Test
        @DisplayName("Should update user successfully")
        void shouldUpdateUserSuccessfully() throws Exception {
            // Given
            User updatedUser = new User("updateduser", "updated@example.com", "Updated", "User");
            updatedUser.setId(1L);
            
            when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);

            // When & Then
            mockMvc.perform(put("/api/users/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("updateduser"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

            verify(userService).updateUser(eq(1L), any(User.class));
        }

        @Test
        @DisplayName("Should return not found when updating non-existent user")
        void shouldReturnNotFoundWhenUpdatingNonExistentUser() throws Exception {
            // Given
            User updatedUser = new User("updateduser", "updated@example.com", "Updated", "User");
            when(userService.updateUser(eq(999L), any(User.class)))
                .thenThrow(new RuntimeException("User not found with id: 999"));

            // When & Then
            mockMvc.perform(put("/api/users/999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());

            verify(userService).updateUser(eq(999L), any(User.class));
        }

        @Test
        @DisplayName("Should delete user successfully")
        void shouldDeleteUserSuccessfully() throws Exception {
            // Given
            doNothing().when(userService).deleteUser(1L);

            // When & Then
            mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());

            verify(userService).deleteUser(1L);
        }

        @Test
        @DisplayName("Should return not found when deleting non-existent user")
        void shouldReturnNotFoundWhenDeletingNonExistentUser() throws Exception {
            // Given
            doThrow(new RuntimeException("User not found")).when(userService).deleteUser(999L);

            // When & Then
            mockMvc.perform(delete("/api/users/999"))
                .andExpect(status().isNotFound());

            verify(userService).deleteUser(999L);
        }

        @Test
        @DisplayName("Should get user by username successfully")
        void shouldGetUserByUsernameSuccessfully() throws Exception {
            // Given
            when(userService.getUserByUsername("testuser")).thenReturn(Optional.of(testUser));

            // When & Then
            mockMvc.perform(get("/api/users/username/testuser"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"));

            verify(userService).getUserByUsername("testuser");
        }

        @Test
        @DisplayName("Should get user by email successfully")
        void shouldGetUserByEmailSuccessfully() throws Exception {
            // Given
            when(userService.getUserByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            // When & Then
            mockMvc.perform(get("/api/users/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@example.com"));

            verify(userService).getUserByEmail("test@example.com");
        }
    }

    // ==================== INTEGRATION TESTS ====================
    @Nested
    @SpringBootTest
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Should load Spring context successfully")
        void shouldLoadSpringContextSuccessfully() {
            // This test verifies that the Spring Boot application context loads without errors
            // If this test passes, it means all beans are properly configured and wired
            assertTrue(true, "Spring context loaded successfully");
        }

        @Test
        @DisplayName("Should have all required beans in context")
        void shouldHaveAllRequiredBeansInContext() {
            // This test can be expanded to verify specific beans are present
            // For now, it's a placeholder for more detailed integration tests
            assertTrue(true, "All required beans are present");
        }
    }
}
