package be.technofutur.exospring.services;

import be.technofutur.exospring.entities.*;
import be.technofutur.exospring.enums.*;
import be.technofutur.exospring.models.commande.*;
import be.technofutur.exospring.models.mission.MissionDto;
import be.technofutur.exospring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@RequiredArgsConstructor
public class CommandeService {
    private final CommandeRepository commandes;
    private final LigneCommandeRepository billets;
    private final LignePanierRepository panier;
    private final MissionRepository missions;
    private final UtilisateurRepository utilisateurs;

    @Transactional
    public Long confirmer(Long utilisateurId) {
        Utilisateur u = utilisateurs.verrouiller(utilisateurId).orElseThrow();
        List<LignePanier> lignes = panier.findByPanierUtilisateurIdOrderById(utilisateurId);
        if (lignes.isEmpty()) throw new IllegalArgumentException("Panier vide");
        Map<Long, Long> quantites = new TreeMap<>();
        for (LignePanier l : lignes) quantites.merge(l.getMission().getId(), 1L, Long::sum);
        for (var entree : quantites.entrySet()) {
            Mission m = missions.verrouiller(entree.getKey()).orElseThrow();
            if (!MissionDto.fromEntity(m).reservable() || m.getPlacesDisponibles() < entree.getValue()) {
                throw new IllegalArgumentException("Places insuffisantes ou mission indisponible : " + m.getNom());
            }
            m.setPlacesDisponibles(m.getPlacesDisponibles() - entree.getValue().intValue());
        }
        Commande c = new Commande();
        c.setUtilisateur(u);
        c.setStatut(StatutCommande.CONFIRMEE);
        for (LignePanier l : lignes) {
            LigneCommande billet = new LigneCommande();
            billet.setCommande(c);
            billet.setMission(l.getMission());
            billet.setNomInscrit(l.getNomInscrit());
            billet.setPrix(l.getMission().getPrix());
            billet.setStatut(StatutInscription.EN_ATTENTE);
            c.getLignes().add(billet);
        }
        commandes.save(c);
        panier.deleteAll(lignes);
        return c.getId();
    }

    @Transactional(readOnly = true)
    public List<CommandeDto> lister(Long utilisateurId) {
        return commandes.findByUtilisateurIdOrderByIdDesc(utilisateurId).stream().map(CommandeDto::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public CommandeDto detail(Long id, Long utilisateurId) {
        Commande c = commandes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!c.getUtilisateur().getId().equals(utilisateurId)) throw new AccessDeniedException("Accès refusé");
        return CommandeDto.fromEntity(c);
    }

    @Transactional(readOnly = true)
    public List<LigneCommandeDto> parMission(Long id, Long utilisateurId) {
        Mission m = missions.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        verifierAstronaute(m, utilisateurId);
        return billets.findByMissionIdOrderById(id).stream().map(LigneCommandeDto::fromEntity).toList();
    }

    @Transactional
    public void deposer(Long missionId, Long billetId, Long utilisateurId) {
        Mission m = missions.verrouiller(missionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        verifierAstronaute(m, utilisateurId);
        if (m.getStatut() != StatutMission.EN_VOL) throw new IllegalArgumentException("La mission doit être en vol");
        LigneCommande l = billets.findById(billetId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!l.getMission().getId().equals(missionId)) throw new AccessDeniedException("Accès refusé");
        l.setStatut(StatutInscription.DEPOSEE);
    }

    private void verifierAstronaute(Mission m, Long id) {
        if (!m.getAstronaute().getId().equals(id)) throw new AccessDeniedException("Accès refusé");
    }
}
