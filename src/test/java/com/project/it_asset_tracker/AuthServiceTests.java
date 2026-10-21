
package com.project.it_asset_tracker;

import com.project.it_asset_tracker.dto.LoginRequest;
import com.project.it_asset_tracker.dto.RegisterRequest;
import com.project.it_asset_tracker.entity.Role;
import com.project.it_asset_tracker.entity.User;
import com.project.it_asset_tracker.repository.UserRepository;
import com.project.it_asset_tracker.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTests {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void registerShouldSaveUserWithEncodedPasswordAndStaffRole() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser_registration");
        request.setPassword("StrongPass123!");

        User registeredUser = authService.register(request);

        assertNotNull(registeredUser.getId());
        assertEquals(
                "testuser_registration",
                registeredUser.getUsername()
        );
        assertEquals(Role.ROLE_STAFF, registeredUser.getRole());

        assertNotEquals(
                "StrongPass123!",
                registeredUser.getPassword()
        );

        assertTrue(
                passwordEncoder.matches(
                        "StrongPass123!",
                        registeredUser.getPassword()
                )
        );

        assertTrue(
                userRepository.existsByUsername(
                        "testuser_registration"
                )
        );
    }

    @Test
    void registerShouldRejectDuplicateUsername() {
        RegisterRequest firstRequest = new RegisterRequest();
        firstRequest.setUsername("duplicate_test_user");
        firstRequest.setPassword("StrongPass123!");

        authService.register(firstRequest);

        RegisterRequest secondRequest = new RegisterRequest();
        secondRequest.setUsername("duplicate_test_user");
        secondRequest.setPassword("AnotherPass123!");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.register(secondRequest)
        );

        assertEquals(
                "Username is already taken",
                exception.getMessage()
        );
    }

    @Test
    void loginShouldSucceedWithCorrectCredentials() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("login_test_user");
        registerRequest.setPassword("StrongPass123!");

        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("login_test_user");
        loginRequest.setPassword("StrongPass123!");

        User loggedInUser = authService.login(loginRequest);

        assertNotNull(loggedInUser);
        assertNotNull(loggedInUser.getId());
        assertEquals("login_test_user", loggedInUser.getUsername());
        assertEquals(Role.ROLE_STAFF, loggedInUser.getRole());
    }

    @Test
    void loginShouldRejectIncorrectPassword() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("wrong_password_test");
        registerRequest.setPassword("StrongPass123!");

        authService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wrong_password_test");
        loginRequest.setPassword("WrongPass123!");

        assertThrows(
                AuthenticationException.class,
                () -> authService.login(loginRequest)
        );
    }
}
