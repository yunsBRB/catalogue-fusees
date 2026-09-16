package be.technofutur.exospring.repositories;

import be.technofutur.exospring.entities.LigneCommande;
import be.technofutur.exospring.enums.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {
    List<LigneCommande> findByMissionIdOrderById(Long id);
    boolean existsByMissionId(Long id);
    boolean existsByMissionIdAndStatut(Long id, StatutInscription statut);
}
