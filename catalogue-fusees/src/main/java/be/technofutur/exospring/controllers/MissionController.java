package be.technofutur.exospring.controllers;

import be.technofutur.exospring.models.mission.*;
import be.technofutur.exospring.models.panier.AjoutPanierForm;
import be.technofutur.exospring.enums.StatutMission;
import be.technofutur.exospring.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequiredArgsConstructor
public class MissionController {
    private final MissionService missions;
    private final FuseeService fusees;
    private final AuthService auth;

    @GetMapping("/missions")
    public String index(@ModelAttribute("filtre") MissionFilter filtre, Model model) {
        model.addAttribute("missions", missions.lister(filtre));
        model.addAttribute("statuts", StatutMission.values());
        return "mission/index";
    }

    @GetMapping("/missions/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("mission", missions.detail(id));
        model.addAttribute("form", new AjoutPanierForm());
        return "mission/details";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping({"/admin/missions/ajouter", "/admin/missions/{id}/modifier"})
    public String formulaire(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("form", id == null ? new MissionForm() : missions.formulaire(id));
        preparer(id, model);
        return "mission/form";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping({"/admin/missions/ajouter", "/admin/missions/{id}/modifier"})
    public String enregistrer(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") MissionForm form, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                missions.enregistrer(id, form);
                return "redirect:/missions";
            } catch (IllegalArgumentException e) {
                result.reject("mission", e.getMessage());
            }
        }
        preparer(id, model);
        return "mission/form";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/missions/{id}/supprimer")
    public String supprimer(@PathVariable Long id) {
        missions.supprimer(id);
        return "redirect:/missions";
    }

    private void preparer(Long id, Model model) {
        model.addAttribute("id", id);
        model.addAttribute("fusees", fusees.lister());
        model.addAttribute("astronautes", auth.astronautes());
        model.addAttribute("statuts", StatutMission.values());
    }
}
