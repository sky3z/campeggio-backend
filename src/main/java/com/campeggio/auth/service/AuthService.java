package com.campeggio.auth.service;

import com.campeggio.auth.dto.*;
import com.campeggio.exceptions.ConflictException;
import com.campeggio.security.JwtUtil;
import com.campeggio.users.entity.Ospite;
import com.campeggio.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authManager;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new ConflictException("Email già registrata: " + req.getEmail());

        Ospite ospite = new Ospite();
        ospite.setEmail(req.getEmail());
        ospite.setPassword(passwordEncoder.encode(req.getPassword()));
        ospite.setName(req.getName());
        ospite.setSurname(req.getSurname());
        ospite.setPhone(req.getPhone());
        ospite.setNationality(req.getNationality());

        userRepository.save(ospite);
        String token = jwtUtil.generateToken(ospite);
        return new AuthResponse(token, ospite.getEmail(), "OSPITE");
    }

    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        var user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato"));
        String token = jwtUtil.generateToken(user);
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
