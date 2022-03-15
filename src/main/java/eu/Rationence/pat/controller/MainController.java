package eu.Rationence.pat.controller;

import eu.Rationence.pat.service.UtentiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class MainController {
    private final UtentiService utentiService;

    public MainController(UtentiService utentiService) {
        this.utentiService = utentiService;
    }

    @RequestMapping ("/")
    public String index() {
        return "index.html";
    }

    @GetMapping ("/utenti")
    public String utenti(Model model) {
        model.addAttribute("utenti", utentiService.findAll());
        System.out.println(model.getAttribute("utenti"));
        return "utenti.html";
    }

    @RequestMapping("/login")
    public String login() {
        return "login.html";
    }

    @RequestMapping("/login-error.html")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "login.html";
    }

}
