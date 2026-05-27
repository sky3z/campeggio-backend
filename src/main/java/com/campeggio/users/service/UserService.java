package com.campeggio.users.service;

import com.campeggio.exceptions.ResourceNotFoundException;
import com.campeggio.users.dto.UserResponseDTO;
import com.campeggio.users.entity.User;
import com.campeggio.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponseDTO getMe(User currentUser) {
        return UserResponseDTO.from(currentUser);
    }

    public UserResponseDTO updateAvatar(User currentUser, String imageUrl) {
        currentUser.setProfileImage(imageUrl);
        userRepository.save(currentUser);
        return UserResponseDTO.from(currentUser);
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserResponseDTO::from);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con id: " + id));
        return UserResponseDTO.from(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("Utente non trovato con id: " + id);
        userRepository.deleteById(id);
    }
}
