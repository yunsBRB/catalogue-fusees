package be.technofutur.exospring.services;

import be.technofutur.exospring.entities.*;
import be.technofutur.exospring.models.panier.*;
import be.technofutur.exospring.models.mission.MissionDto;
import be.technofutur.exospring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class PanierService {
    private final PanierRepository paniers;
    private final LignePanierRepository lignes;
    private final MissionRepository missions;
    private final UtilisateurRepository utilisateurs;

    @Transactional(readOnly = true)
    public PanierDto detail(Long utilisateurId) {
        return new PanierDto(lignes.findByPanierUtilisateurIdOrderById(utilisateurId).stream()
                .map(LignePanierDto::fromEntity).toList());
    }

    @Transactional
    public void ajouter(Long utilisateurId, Long missionId, AjoutPanierForm form) {
        Utilisateur u = utilisateurs.verrouiller(utilisateurId).orElseThrow();
        Mission m = missions.verrouiller(missionId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!MissionDto.fromEntity(m).reservable()) throw new IllegalArgumentException("Mission indisponible");
        Panier p = paniers.findByUtilisateurId(utilisateurId).orElseGet(() -> {
            Panier nouveau = new Panier();
            nouveau.setUtilisateur(u);
            return paniers.save(nouveau);
        });
        LignePanier l = new LignePanier();
        l.setPanier(p);
        l.setMission(m);
        l.setNomInscrit(form.getNomInscrit().strip());
        lignes.save(l);
    }

    @Transactional
    public void retirer(Long utilisateurId, Long id) {
        utilisateurs.verrouiller(utilisateurId).orElseThrow();
        LignePanier l = lignes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!l.getPanier().getUtilisateur().getId().equals(utilisateurId)) throw new AccessDeniedException("Accès refusé");
        lignes.delete(l);
    }
}
