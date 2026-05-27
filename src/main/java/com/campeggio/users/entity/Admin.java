package com.campeggio.users.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admin")
@DiscriminatorValue("ADMIN")
@Getter @Setter @NoArgsConstructor
public class Admin extends User {
}
