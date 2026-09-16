package be.technofutur.exospring.models.commande;

import be.technofutur.exospring.entities.Commande;
import be.technofutur.exospring.enums.StatutCommande;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CommandeDto(Long id, LocalDateTime date, StatutCommande statut, List<LigneCommandeDto> lignes) {
    public static CommandeDto fromEntity(Commande c) {
        return new CommandeDto(c.getId(), c.getCreatedAt(), c.getStatut(),
                c.getLignes().stream().map(LigneCommandeDto::fromEntity).toList());
    }

    public BigDecimal total() {
        return lignes.stream().map(LigneCommandeDto::prix).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
