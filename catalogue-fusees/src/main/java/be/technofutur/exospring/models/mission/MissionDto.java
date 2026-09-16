package be.technofutur.exospring.models.mission;

import be.technofutur.exospring.entities.Mission;
import be.technofutur.exospring.enums.StatutMission;
import java.math.BigDecimal;
import java.time.LocalDate;

public record MissionDto(Long id, String nom, LocalDate dateDepart, BigDecimal prix,
                         int placesDisponibles, StatutMission statut, Long fuseeId,
                         String fuseeNom, String astronauteNom) {
    public static MissionDto fromEntity(Mission m) {
        return new MissionDto(m.getId(), m.getNom(), m.getDateDepart(), m.getPrix(),
                m.getPlacesDisponibles(), m.getStatut(), m.getFusee().getId(),
                m.getFusee().getNom(), m.getAstronaute().getUsername());
    }

    public boolean reservable() {
        return statut == StatutMission.PLANIFIEE && placesDisponibles > 0
                && !dateDepart.isBefore(LocalDate.now());
    }
}
