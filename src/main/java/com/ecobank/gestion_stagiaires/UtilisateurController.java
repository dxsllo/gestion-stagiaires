package com.ecobank.gestion_stagiaires;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.Random;

@Controller
public class UtilisateurController {
    @Autowired
    private UtilisateurRepository UtilisateurRepository;

    @Autowired
    private EmailService emailService;

    @GetMapping("/connexion")
    public String afficherConnexion() {
        return "connexion";
    }

    @PostMapping("/connexion")
    public String login(@RequestParam String identifiant,
                        @RequestParam String motDePasse,
                        HttpSession session,
                        Model model) {
        Utilisateur utilisateur = UtilisateurRepository.findByIdentifiantAndMotDePasse(identifiant, motDePasse);

        if (utilisateur != null) {
            // Générer un code OTP à 6 chiffres
            String otp = String.format("%06d", new Random().nextInt(999999));

            // Sauvegarder le code et son expiration (5 minutes)
            utilisateur.setOtp(otp);
            utilisateur.setOtpExpiration(System.currentTimeMillis() + 5 * 60 * 1000);
            UtilisateurRepository.save(utilisateur);

            // Envoyer le code par email
            emailService.envoyerOtp(utilisateur.getEmail(), otp);

            // Stocker l'id temporairement dans la session
            session.setAttribute("otp_user_id", utilisateur.getId());

            return "redirect:/verification-otp";
        } else {
            model.addAttribute("erreur", "Identifiant ou mot de passe incorrect");
            return "connexion";
        }
    }

    @GetMapping("/verification-otp")
    public String afficherVerificationOtp(HttpSession session) {
        if (session.getAttribute("otp_user_id") == null) {
            return "redirect:/connexion";
        }
        return "verification_otp";
    }

    @PostMapping("/verification-otp")
    public String verifierOtp(@RequestParam String code,
                              HttpSession session,
                              Model model) {
        Long userId = (Long) session.getAttribute("otp_user_id");
        if (userId == null) {
            return "redirect:/connexion";
        }

        Utilisateur utilisateur = UtilisateurRepository.findById(userId).orElse(null);

        if (utilisateur == null) {
            return "redirect:/connexion";
        }

        // Vérifier si le code est correct et non expiré
        if (utilisateur.getOtp() != null
                && utilisateur.getOtp().equals(code)
                && System.currentTimeMillis() < utilisateur.getOtpExpiration()) {

            // Code correct — connecter l'utilisateur
            session.removeAttribute("otp_user_id");
            session.setAttribute("utilisateur", utilisateur);

            // Effacer le code OTP
            utilisateur.setOtp(null);
            utilisateur.setOtpExpiration(0);
            UtilisateurRepository.save(utilisateur);

            return "redirect:/";
        } else {
            model.addAttribute("erreur", "Code incorrect ou expiré");
            return "verification_otp";
        }
    }

    @GetMapping("/deconnexion")
    public String deconnexion(HttpSession session) {
        session.invalidate();
        return "redirect:/connexion";
    }
    @GetMapping("/renvoyer-otp")
    public String renvoyerOtp(HttpSession session) {
        Long userId = (Long) session.getAttribute("otp_user_id");
        if (userId == null) {
            return "redirect:/connexion";
        }

        Utilisateur utilisateur = UtilisateurRepository.findById(userId).orElse(null);
        if (utilisateur != null) {
            String otp = String.format("%06d", new Random().nextInt(999999));
            utilisateur.setOtp(otp);
            utilisateur.setOtpExpiration(System.currentTimeMillis() + 5 * 60 * 1000);
            UtilisateurRepository.save(utilisateur);

            try {
                emailService.envoyerOtp(utilisateur.getEmail(), otp);
            } catch (Exception e) {
                // Ignorer si l'envoi échoue
            }
        }

        return "redirect:/verification-otp";
    }
}