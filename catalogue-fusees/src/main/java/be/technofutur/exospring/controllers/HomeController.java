package be.technofutur.exospring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() { return "index"; }

    @GetMapping("/rockets")
    public String rockets() { return "redirect:/fusees"; }

    @GetMapping("/acces-refuse")
    public String accesRefuse() { return "error/403"; }
}
