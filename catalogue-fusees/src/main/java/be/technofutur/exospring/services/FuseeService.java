package be.technofutur.exospring.services;

import be.technofutur.exospring.entities.Fusee;
import be.technofutur.exospring.models.fusee.*;
import be.technofutur.exospring.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FuseeService {
    private final FuseeRepository fusees;
    private final MissionRepository missions;

    public List<FuseeDto> lister() {
        return fusees.findAllByOrderByNom().stream().map(FuseeDto::fromEntity).toList();
    }

    public FuseeDto detail(Long id) {
        return FuseeDto.fromEntity(trouver(id));
    }

    public FuseeForm formulaire(Long id) {
        Fusee f = trouver(id);
        FuseeForm form = new FuseeForm();
        form.setNom(f.getNom());
        form.setDescription(f.getDescription());
        form.setImageUrl(f.getImageUrl());
        return form;
    }

    @Transactional
    public void enregistrer(Long id, FuseeForm form) {
        Fusee f = id == null ? new Fusee() : trouver(id);
        f.setNom(form.getNom().strip());
        f.setDescription(form.getDescription().strip());
        f.setImageUrl(form.getImageUrl());
        fusees.save(f);
    }

    @Transactional
    public void supprimer(Long id) {
        Fusee f = trouver(id);
        if (missions.existsByFuseeId(id)) {
            throw new IllegalArgumentException("Cette fusée est liée à une mission");
        }
        fusees.delete(f);
    }

    private Fusee trouver(Long id) {
        return fusees.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
