package be.technofutur.exospring.repositories;

import be.technofutur.exospring.entities.Mission;
import be.technofutur.exospring.enums.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    @Query("select m from Mission m where (:nom is null or lower(m.nom) like lower(concat('%', :nom, '%'))) and (:statut is null or m.statut = :statut) order by m.dateDepart")
    List<Mission> filtrer(String nom, StatutMission statut);
    List<Mission> findByFuseeIdOrderByDateDepart(Long id);
    List<Mission> findByAstronauteIdOrderByDateDepart(Long id);
    boolean existsByFuseeId(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Mission m where m.id = :id")
    Optional<Mission> verrouiller(Long id);
}
