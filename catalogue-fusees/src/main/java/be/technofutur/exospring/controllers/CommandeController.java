package be.technofutur.exospring.controllers;

import be.technofutur.exospring.entities.Utilisateur;
import be.technofutur.exospring.services.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('CLIENT')")
@RequestMapping("/commandes")
public class CommandeController {
    private final CommandeService commandes;

    @GetMapping
    public String index(@AuthenticationPrincipal Utilisateur user, Model model) {
        model.addAttribute("commandes", commandes.lister(user.getId()));
        return "commande/index";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal Utilisateur user, Model model) {
        model.addAttribute("commande", commandes.detail(id, user.getId()));
        return "commande/details";
    }

    @PostMapping
    public String confirmer(@AuthenticationPrincipal Utilisateur user, RedirectAttributes redirect) {
        try {
            return "redirect:/commandes/" + commandes.confirmer(user.getId());
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/panier";
        }
    }
}
