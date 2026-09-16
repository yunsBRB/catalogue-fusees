package be.technofutur.exospring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;
import be.technofutur.exospring.enums.StatutCommande;

@Getter
@Setter
@Entity
public class Commande extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Utilisateur utilisateur;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCommande statut;
    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL)
    @OrderBy("id")
    private List<LigneCommande> lignes = new ArrayList<>();
}
