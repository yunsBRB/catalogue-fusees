package be.technofutur.exospring.models.panier;

import be.technofutur.exospring.entities.LignePanier;
import java.math.BigDecimal;

public record LignePanierDto(Long id, String nomInscrit, String missionNom, BigDecimal prix) {
    public static LignePanierDto fromEntity(LignePanier l) {
        return new LignePanierDto(l.getId(), l.getNomInscrit(), l.getMission().getNom(), l.getMission().getPrix());
    }
}
