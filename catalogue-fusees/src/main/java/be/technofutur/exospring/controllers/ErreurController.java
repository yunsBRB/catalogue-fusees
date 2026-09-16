package be.technofutur.exospring.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.ui.Model;
import org.springframework.http.HttpStatus;
import org.springframework.dao.DataIntegrityViolationException;

@ControllerAdvice
public class ErreurController {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String metier(IllegalArgumentException e, Model model) {
        model.addAttribute("erreur", e.getMessage());
        return "error/400";
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflit(Model model) {
        model.addAttribute("erreur", "Ces données sont déjà utilisées. Rechargez la page.");
        return "error/400";
    }
}
