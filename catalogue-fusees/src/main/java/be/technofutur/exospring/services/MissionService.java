package be.technofutur.exospring.services;

import be.technofutur.exospring.entities.*;
import be.technofutur.exospring.enums.*;
import be.technofutur.exospring.models.mission.*;
import be.technofutur.exospring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {
    private final MissionRepository missions;
    private final FuseeRepository fusees;
    private final UtilisateurRepository utilisateurs;
    private final LigneCommandeRepository billets;
    private final LignePanierRepository paniers;

    public List<MissionDto> lister(MissionFilter filtre) {
        return missions.filtrer(filtre.nom(), filtre.statut()).stream().map(MissionDto::fromEntity).toList();
    }

    public List<MissionDto> parFusee(Long id) {
        return missions.findByFuseeIdOrderByDateDepart(id).stream().map(MissionDto::fromEntity).toList();
    }

    public List<MissionDto> parAstronaute(Long id) {
        return missions.findByAstronauteIdOrderByDateDepart(id).stream().map(MissionDto::fromEntity).toList();
    }

    public MissionDto detail(Long id) {
        return MissionDto.fromEntity(trouver(id));
    }

    public MissionDto detailAstronaute(Long id, Long utilisateurId) {
        Mission m = trouver(id);
        verifierAstronaute(m, utilisateurId);
        return MissionDto.fromEntity(m);
    }

    public MissionForm formulaire(Long id) {
        Mission m = trouver(id);
        MissionForm f = new MissionForm();
        f.setNom(m.getNom());
        f.setDateDepart(m.getDateDepart());
        f.setPrix(m.getPrix());
        f.setPlacesDisponibles(m.getPlacesDisponibles());
        f.setFuseeId(m.getFusee().getId());
        f.setAstronauteId(m.getAstronaute().getId());
        f.setStatut(m.getStatut());
        return f;
    }

    @Transactional
    public void enregistrer(Long id, MissionForm form) {
        Mission m = id == null ? new Mission() : missions.verrouiller(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (form.getStatut() == StatutMission.PLANIFIEE && form.getDateDepart().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Choisissez une date à venir");
        }
        if (id != null) {
            if (m.getStatut() != StatutMission.PLANIFIEE && m.getStatut() != form.getStatut()) {
                throw new IllegalArgumentException("Utilisez le suivi astronaute pour terminer la mission");
            }
            if (form.getStatut() == StatutMission.ANNULEE && billets.existsByMissionId(id)) {
                throw new IllegalArgumentException("Cette mission contient déjà des billets");
            }
            if (m.getStatut() == StatutMission.PLANIFIEE && form.getStatut() == StatutMission.TERMINEE) {
                throw new IllegalArgumentException("La mission doit d’abord être lancée");
            }
        } else if (form.getStatut() != StatutMission.PLANIFIEE) {
            throw new IllegalArgumentException("Une nouvelle mission doit être planifiée");
        }
        Utilisateur astronaute = utilisateurs.findById(form.getAstronauteId())
                .filter(u -> u.getRole() == Role.ASTRONAUTE)
                .orElseThrow(() -> new IllegalArgumentException("Astronaute introuvable"));
        Fusee fusee = fusees.findById(form.getFuseeId())
                .orElseThrow(() -> new IllegalArgumentException("Fusée introuvable"));
        m.setNom(form.getNom().strip());
        m.setDateDepart(form.getDateDepart());
        m.setPrix(form.getPrix());
        m.setPlacesDisponibles(form.getPlacesDisponibles());
        m.setStatut(form.getStatut());
        m.setFusee(fusee);
        m.setAstronaute(astronaute);
        missions.save(m);
    }

    @Transactional
    public void supprimer(Long id) {
        Mission m = missions.verrouiller(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (billets.existsByMissionId(id) || paniers.existsByMissionId(id)) {
            throw new IllegalArgumentException("Cette mission est utilisée dans un panier ou une commande");
        }
        missions.delete(m);
    }

    @Transactional
    public void avancer(Long id, Long utilisateurId) {
        Mission m = missions.verrouiller(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        verifierAstronaute(m, utilisateurId);
        if (m.getStatut() == StatutMission.PLANIFIEE) {
            m.setStatut(StatutMission.EN_VOL);
        } else if (m.getStatut() == StatutMission.EN_VOL) {
            if (billets.existsByMissionIdAndStatut(id, StatutInscription.EN_ATTENTE)) {
                throw new IllegalArgumentException("Déposez tous les noms avant de terminer");
            }
            m.setStatut(StatutMission.TERMINEE);
        } else {
            throw new IllegalArgumentException("Cette mission est clôturée");
        }
    }

    private void verifierAstronaute(Mission m, Long id) {
        if (!m.getAstronaute().getId().equals(id)) throw new AccessDeniedException("Accès refusé");
    }

    private Mission trouver(Long id) {
        return missions.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
