package be.technofutur.exospring.repositories;

import be.technofutur.exospring.entities.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByUtilisateurIdOrderByIdDesc(Long id);
}
