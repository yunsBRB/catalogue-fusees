package be.technofutur.exospring.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
public class LignePanier extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Panier panier;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Mission mission;
    @Column(nullable = false, length = 40)
    private String nomInscrit;
}
