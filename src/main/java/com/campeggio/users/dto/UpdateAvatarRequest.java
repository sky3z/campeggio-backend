package com.campeggio.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateAvatarRequest {
    @NotBlank(message = "L'URL dell'immagine non può essere vuoto")
    private String profileImageUrl;
}
