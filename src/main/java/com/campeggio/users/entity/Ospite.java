package com.campeggio.users.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "ospite")
@DiscriminatorValue("OSPITE")
@Getter @Setter @NoArgsConstructor
public class Ospite extends User {

    private String phone;
    private String nationality;
    private LocalDate birthDate;
    private String documentNumber;
}
