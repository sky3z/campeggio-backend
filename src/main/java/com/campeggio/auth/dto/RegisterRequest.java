package com.campeggio.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @Email(message = "Email non valida")
    @NotBlank(message = "Email obbligatoria")
    private String email;

    @NotBlank(message = "Password obbligatoria")
    @Size(min = 8, message = "La password deve essere di almeno 8 caratteri")
    private String password;

    @NotBlank(message = "Nome obbligatorio")
    private String name;

    @NotBlank(message = "Cognome obbligatorio")
    private String surname;

    private String phone;
    private String nationality;
}
