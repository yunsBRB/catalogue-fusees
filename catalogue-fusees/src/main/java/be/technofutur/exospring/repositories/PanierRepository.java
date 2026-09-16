package be.technofutur.exospring.repositories;

import be.technofutur.exospring.entities.Panier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PanierRepository extends JpaRepository<Panier, Long> {
    Optional<Panier> findByUtilisateurId(Long id);
}
