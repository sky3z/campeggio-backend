package com.campeggio.bar.entity;

import com.campeggio.users.entity.Ospite;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ordine_bar")
@Getter
@Setter
@NoArgsConstructor
public class OrdineBar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ospite_id", nullable = false)
    private Ospite ospite;

    @Column(length = 20)
    private String numeroPiazzola;

    @OneToMany(mappedBy = "ordine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoceOrdine> voci = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totale = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatoOrdine stato = StatoOrdine.IN_ATTESA;

    @Column(length = 500)
    private String note;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completatoAt;
}
