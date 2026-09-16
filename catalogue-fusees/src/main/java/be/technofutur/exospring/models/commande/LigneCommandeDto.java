package be.technofutur.exospring.models.commande;

import be.technofutur.exospring.entities.LigneCommande;
import be.technofutur.exospring.enums.StatutInscription;
import be.technofutur.exospring.enums.StatutMission;
import java.math.BigDecimal;
import java.time.LocalDate;

public record LigneCommandeDto(Long id, String nomInscrit, String missionNom,
                               LocalDate dateDepart, BigDecimal prix,
                               StatutInscription statut, StatutMission statutMission) {
    public static LigneCommandeDto fromEntity(LigneCommande l) {
        return new LigneCommandeDto(l.getId(), l.getNomInscrit(), l.getMission().getNom(),
                l.getMission().getDateDepart(), l.getPrix(), l.getStatut(), l.getMission().getStatut());
    }
}
