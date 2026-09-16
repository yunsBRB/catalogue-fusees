package be.technofutur.exospring.repositories;

import be.technofutur.exospring.entities.LignePanier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LignePanierRepository extends JpaRepository<LignePanier, Long> {
    List<LignePanier> findByPanierUtilisateurIdOrderById(Long id);
    boolean existsByMissionId(Long id);
}
