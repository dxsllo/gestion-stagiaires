package com.ecobank.gestion_stagiaires;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

@Controller
public class UtilisateurController {
    @Autowired
    private UtilisateurRepository UtilisateurRepository;

    @GetMapping("/connexion")
    public String afficherConnexion(){
        return "connexion";
    }

    @PostMapping("/connexion")
    public String login(@RequestParam String identifiant,
                        @RequestParam String motDePasse,
                        HttpSession session,
                        Model model) {
        Utilisateur utilisateur = UtilisateurRepository.findByIdentifiantAndMotDePasse(identifiant, motDePasse);

        if (utilisateur != null) {
            session.setAttribute("utilisateur", utilisateur);
            return "redirect:/";
        } else {
            model.addAttribute("erreur", "Identifiant ou mot de passe incorrect");
            return "connexion";
        }
    }

    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session){
        session.invalidate();
        return "redirect:/connexion";
    }

}
