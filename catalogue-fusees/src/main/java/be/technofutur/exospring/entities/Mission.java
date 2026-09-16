package be.technofutur.exospring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.math.BigDecimal;
import be.technofutur.exospring.enums.StatutMission;

@Getter
@Setter
@Entity
public class Mission extends BaseEntity {
    @Column(nullable = false, length = 60)
    private String nom;
    @Column(nullable = false)
    private LocalDate dateDepart;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;
    @Column(nullable = false)
    private int placesDisponibles;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutMission statut;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Fusee fusee;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Utilisateur astronaute;
}
