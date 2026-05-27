package com.campeggio.auth;

import com.campeggio.auth.dto.*;
import com.campeggio.auth.service.AuthService;
import com.campeggio.exceptions.ConflictException;
import com.campeggio.security.JwtUtil;
import com.campeggio.users.entity.*;
import com.campeggio.users.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AuthenticationManager authManager;

    @InjectMocks
    private AuthService authService;

    // ─── REGISTER ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("register: registrazione con successo restituisce token")
    void register_success() {
        // arrange
        RegisterRequest req = new RegisterRequest();
        req.setEmail("nuovo@example.com");
        req.setPassword("Password1!");
        req.setName("Mario");
        req.setSurname("Rossi");

        when(userRepository.existsByEmail("nuovo@example.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");
        when(userRepository.save(any(Ospite.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtUtil.generateToken(any())).thenReturn("jwt-token-xyz");

        // act
        AuthResponse response = authService.register(req);

        // assert
        assertThat(response.getToken()).isEqualTo("jwt-token-xyz");
        assertThat(response.getEmail()).isEqualTo("nuovo@example.com");
        assertThat(response.getRole()).isEqualTo("OSPITE");
        verify(userRepository).save(any(Ospite.class));
    }

    @Test
    @DisplayName("register: email già esistente lancia ConflictException")
    void register_emailDuplicata_lanceConflictException() {
        // arrange
        RegisterRequest req = new RegisterRequest();
        req.setEmail("esistente@example.com");
        req.setPassword("Password1!");
        req.setName("Luca");
        req.setSurname("Bianchi");

        when(userRepository.existsByEmail("esistente@example.com")).thenReturn(true);

        // act & assert
        assertThatThrownBy(() -> authService.register(req))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Email già registrata");

        verify(userRepository, never()).save(any());
    }

    // ─── LOGIN ────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("login: credenziali corrette restituisce token con ruolo")
    void login_success() {
        // arrange
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@campeggio.it");
        req.setPassword("Admin123!");

        Admin admin = new Admin();
        admin.setEmail("admin@campeggio.it");
        admin.setPassword("hashed");
        admin.setName("Marco");
        admin.setSurname("Admin");

        when(authManager.authenticate(any())).thenReturn(null);
        when(userRepository.findByEmail("admin@campeggio.it")).thenReturn(Optional.of(admin));
        when(jwtUtil.generateToken(admin)).thenReturn("admin-jwt-token");

        // act
        AuthResponse response = authService.login(req);

        // assert
        assertThat(response.getToken()).isEqualTo("admin-jwt-token");
        assertThat(response.getEmail()).isEqualTo("admin@campeggio.it");
        assertThat(response.getRole()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("login: credenziali errate lancia BadCredentialsException")
    void login_credenzialierrate_lanceEccezione() {
        // arrange
        LoginRequest req = new LoginRequest();
        req.setEmail("user@example.com");
        req.setPassword("wrong");

        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenziali errate"));

        // act & assert
        assertThatThrownBy(() -> authService.login(req))
                .isInstanceOf(BadCredentialsException.class);
    }
}
