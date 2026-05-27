package com.campeggio.users.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "staff")
@DiscriminatorValue("STAFF")
@Getter @Setter @NoArgsConstructor
public class Staff extends User {

    public enum Department { RECEPTION, BAR, MANUTENZIONE }

    @Enumerated(EnumType.STRING)
    private Department department;
}
