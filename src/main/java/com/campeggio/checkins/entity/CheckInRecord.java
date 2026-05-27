package com.campeggio.checkins.entity;

import com.campeggio.reservations.entity.Prenotazione;
import com.campeggio.users.entity.Staff;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "check_in_record")
@Getter
@Setter
@NoArgsConstructor
public class CheckInRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prenotazione_id", nullable = false, unique = true)
    private Prenotazione prenotazione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "staff_id")
    private Staff registratoDa;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoCheckIn tipo;

    @Column(length = 500)
    private String note;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
