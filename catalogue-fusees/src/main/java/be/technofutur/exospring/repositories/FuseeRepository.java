package be.technofutur.exospring.repositories;

import be.technofutur.exospring.entities.Fusee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FuseeRepository extends JpaRepository<Fusee, Long> {
    List<Fusee> findAllByOrderByNom();
}
