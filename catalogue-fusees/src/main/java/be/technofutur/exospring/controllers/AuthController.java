package be.technofutur.exospring.controllers;

import be.technofutur.exospring.models.auth.InscriptionForm;
import be.technofutur.exospring.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.dao.DataIntegrityViolationException;

@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService auth;

    @GetMapping("/login")
    public String login() { return "auth/login"; }

    @PreAuthorize("isAnonymous()")
    @GetMapping("/inscription")
    public String inscription(Model model) {
        model.addAttribute("form", new InscriptionForm());
        return "auth/inscription";
    }

    @PreAuthorize("isAnonymous()")
    @PostMapping("/inscription")
    public String inscription(@Valid @ModelAttribute("form") InscriptionForm form, BindingResult result) {
        if (!result.hasErrors()) {
            try {
                auth.inscrire(form);
                return "redirect:/login?inscrit";
            } catch (IllegalArgumentException | DataIntegrityViolationException e) {
                result.reject("compte", "Pseudo ou email déjà utilisé");
            }
        }
        return "auth/inscription";
    }
}
