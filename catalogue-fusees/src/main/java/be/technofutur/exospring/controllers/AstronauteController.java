package be.technofutur.exospring.controllers;

import be.technofutur.exospring.entities.Utilisateur;
import be.technofutur.exospring.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ASTRONAUTE')")
@RequestMapping("/astronaute")
public class AstronauteController {
    private final MissionService missions;
    private final CommandeService commandes;

    @GetMapping
    public String index(@AuthenticationPrincipal Utilisateur user, Model model) {
        model.addAttribute("missions", missions.parAstronaute(user.getId()));
        return "astronaute/index";
    }

    @GetMapping("/missions/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal Utilisateur user, Model model) {
        model.addAttribute("mission", missions.detailAstronaute(id, user.getId()));
        model.addAttribute("billets", commandes.parMission(id, user.getId()));
        return "astronaute/mission-details";
    }

    @PostMapping("/missions/{id}/avancer")
    public String avancer(@PathVariable Long id, @AuthenticationPrincipal Utilisateur user, RedirectAttributes redirect) {
        try {
            missions.avancer(id, user.getId());
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/astronaute/missions/" + id;
    }

    @PostMapping("/missions/{id}/billets/{billetId}/deposer")
    public String deposer(@PathVariable Long id, @PathVariable Long billetId,
            @AuthenticationPrincipal Utilisateur user, RedirectAttributes redirect) {
        try {
            commandes.deposer(id, billetId, user.getId());
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/astronaute/missions/" + id;
    }
}
