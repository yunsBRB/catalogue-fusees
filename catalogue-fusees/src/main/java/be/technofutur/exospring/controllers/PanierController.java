package be.technofutur.exospring.controllers;

import be.technofutur.exospring.entities.Utilisateur;
import be.technofutur.exospring.models.panier.AjoutPanierForm;
import be.technofutur.exospring.services.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CLIENT')")
@RequestMapping("/panier")
public class PanierController {
    private final PanierService panier;
    private final MissionService missions;

    @GetMapping
    public String index(@AuthenticationPrincipal Utilisateur user, Model model) {
        model.addAttribute("panier", panier.detail(user.getId()));
        return "panier/index";
    }

    @PostMapping("/ajouter/{id}")
    public String ajouter(@PathVariable Long id, @AuthenticationPrincipal Utilisateur user,
            @Valid @ModelAttribute("form") AjoutPanierForm form, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            try {
                panier.ajouter(user.getId(), id, form);
                return "redirect:/panier";
            } catch (IllegalArgumentException e) {
                result.reject("panier", e.getMessage());
            }
        }
        model.addAttribute("mission", missions.detail(id));
        return "mission/details";
    }

    @PostMapping("/retirer/{id}")
    public String retirer(@PathVariable Long id, @AuthenticationPrincipal Utilisateur user) {
        panier.retirer(user.getId(), id);
        return "redirect:/panier";
    }
}
