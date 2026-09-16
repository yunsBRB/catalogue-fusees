package be.technofutur.exospring.controllers;

import be.technofutur.exospring.models.fusee.FuseeForm;
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
public class FuseeController {
    private final FuseeService fusees;
    private final MissionService missions;

    @GetMapping("/fusees")
    public String index(Model model) {
        model.addAttribute("fusees", fusees.lister());
        return "fusee/index";
    }

    @GetMapping("/fusees/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("fusee", fusees.detail(id));
        model.addAttribute("missions", missions.parFusee(id));
        return "fusee/details";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping({"/admin/fusees/ajouter", "/admin/fusees/{id}/modifier"})
    public String formulaire(@PathVariable(required = false) Long id, Model model) {
        model.addAttribute("form", id == null ? new FuseeForm() : fusees.formulaire(id));
        model.addAttribute("id", id);
        return "fusee/form";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping({"/admin/fusees/ajouter", "/admin/fusees/{id}/modifier"})
    public String enregistrer(@PathVariable(required = false) Long id,
            @Valid @ModelAttribute("form") FuseeForm form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            return "fusee/form";
        }
        fusees.enregistrer(id, form);
        return "redirect:/fusees";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/admin/fusees/{id}/supprimer")
    public String supprimer(@PathVariable Long id) {
        fusees.supprimer(id);
        return "redirect:/fusees";
    }
}
