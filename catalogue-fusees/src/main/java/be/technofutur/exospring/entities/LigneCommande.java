package be.technofutur.exospring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import be.technofutur.exospring.enums.StatutInscription;

@Getter
@Setter
@Entity
public class LigneCommande extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Commande commande;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Mission mission;
    @Column(nullable = false, length = 40)
    private String nomInscrit;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInscription statut;
}
