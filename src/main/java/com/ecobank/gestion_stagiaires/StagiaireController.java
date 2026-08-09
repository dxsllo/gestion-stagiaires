package com.ecobank.gestion_stagiaires;

import com.ecobank.gestion_stagiaires.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StagiaireController {

    @Autowired
    private StagiaireRepository stagiaireRepository;

    @GetMapping("/")
    public String listeStagiaires(Model model,HttpSession session) {
        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }
            List<Stagiaire> stagiaires = stagiaireRepository.findAll();
            model.addAttribute("stagiaires", stagiaires);
    return "liste_stagiaires";
    }

    @GetMapping("/ajouter")
    public String afficherFormulaire(Model model, HttpSession session) {
        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }
        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!u.getRole().equals("Administrateur")) {
            return "redirect:/";
        }
        model.addAttribute("stagiaire", new Stagiaire());
        return "ajouter_stagiaire";
    }

    @PostMapping("/enregistrer")
    public String enregistrerStagiaire(Stagiaire stagiaire, Model model, HttpSession session) {
        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }
        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!u.getRole().equals("Administrateur")) {
            return "redirect:/";
        }
        Stagiaire existant = stagiaireRepository.findByEmail(stagiaire.getEmail());
        if (existant != null && (stagiaire.getId() == null || !existant.getId().equals(stagiaire.getId()))) {
            model.addAttribute("erreur", "Cet email est déjà utilisé par un autre stagiaire");
            model.addAttribute("stagiaire", stagiaire);
            return "ajouter_stagiaire";
        }
        // Vérifier le doublon nom + prénom + école
        Stagiaire doublon = stagiaireRepository.findByNomAndPrenomAndEcoleOrigine(
                stagiaire.getNom(), stagiaire.getPrenom(), stagiaire.getEcoleOrigine());
        if (doublon != null && (stagiaire.getId() == null || !doublon.getId().equals(stagiaire.getId()))) {
            model.addAttribute("erreur", "Un stagiaire avec le même nom, prénom et école existe déjà");
            model.addAttribute("stagiaire", stagiaire);
            return "ajouter_stagiaire";
        }
        if (stagiaire.getDateFin() != null && stagiaire.getDateDebut() != null
                && stagiaire.getDateFin().compareTo(stagiaire.getDateDebut()) < 0) {
            model.addAttribute("erreur", "La date de fin ne peut pas être antérieure à la date de début");
            model.addAttribute("stagiaire", stagiaire);
            return "ajouter_stagiaire";
        }
        if (stagiaire.getNbTotalTaches() < 0 || stagiaire.getNbTachesRealisees() < 0) {
            model.addAttribute("erreur", "Le nombre de tâches ne peut pas être négatif");
            model.addAttribute("stagiaire", stagiaire);
            return "ajouter_stagiaire";
        }
        if (stagiaire.getNbTachesRealisees() > stagiaire.getNbTotalTaches()) {
            model.addAttribute("erreur", "Le nombre de tâches réalisées ne peut pas dépasser le nombre total de tâches");
            model.addAttribute("stagiaire", stagiaire);
            return "ajouter_stagiaire";
        }
        // Vérifier la note
        if (stagiaire.getNote() < 0 || stagiaire.getNote() > 20) {
            model.addAttribute("erreur", "La note doit être comprise entre 0 et 20");
            model.addAttribute("stagiaire", stagiaire);
            return "ajouter_stagiaire";
        }
        try {
            stagiaireRepository.save(stagiaire);
        } catch (Exception e) {
            model.addAttribute("erreur", "Cet email est déjà utilisé par un autre stagiaire");
            model.addAttribute("stagiaire", stagiaire);
            return "ajouter_stagiaire";
        }
        return "redirect:/";
    }

    @GetMapping("/details/{id}")
    public String detailsStagiaire(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }
        Stagiaire stagiaire = stagiaireRepository.findById(id).orElse(null);
        model.addAttribute("stagiaire", stagiaire);
        return "details_stagiaire";
    }

    @GetMapping("/modifier/{id}")
    public String afficherModification(@PathVariable Long id, Model model,HttpSession session) {
        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }
        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!u.getRole().equals("Administrateur")) {
            return "redirect:/";
        }
        Stagiaire stagiaire = stagiaireRepository.findById(id).orElse(null);
        model.addAttribute("stagiaire", stagiaire);
        return "modifier_stagiaire";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimerStagiaire(@PathVariable Long id,HttpSession session) {
        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }
        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!u.getRole().equals("Administrateur")) {
            return "redirect:/";
        }
        stagiaireRepository.deleteById(id);
        return "redirect:/";
    }

    @GetMapping("/tableau_de_bord")
    public String afficherTableauDeBord(Model model, HttpSession session) {
        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }
        List<Stagiaire> stagiaires = stagiaireRepository.findAll();

        int totalStagiaires = stagiaires.size();

        int nbMemoiresRediges = 0;
        int nbPresentesDRH = 0;
        int nbPresentesChef = 0;

        for (Stagiaire s : stagiaires) {
            if (s.isMemoireRedige()) {
                nbMemoiresRediges++;
            }
            if (s.isPresenteDRH()) {
                nbPresentesDRH++;
            }
            if (s.isPresenteChefService()) {
                nbPresentesChef++;
            }
        }

        model.addAttribute("totalStagiaires", totalStagiaires);
        model.addAttribute("nbMemoiresRediges", nbMemoiresRediges);
        model.addAttribute("nbPresentesDRH", nbPresentesDRH);
        model.addAttribute("nbPresentesChef", nbPresentesChef);
        model.addAttribute("stagiaires", stagiaires);

        return "tableau_de_bord";
    }

    @GetMapping("/valider-drh/{id}")
    public String validerDRH(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }
        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!u.getRole().equals("DRH") && !u.getRole().equals("Administrateur")) {
            return "redirect:/";
        }
        Stagiaire stagiaire = stagiaireRepository.findById(id).orElse(null);
        if (stagiaire != null) {
            stagiaire.setPresenteDRH(!stagiaire.isPresenteDRH());
            stagiaireRepository.save(stagiaire);
        }
        return "redirect:/details/" + id;
    }

    @GetMapping("/valider-chef/{id}")
    public String validerChef(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }
        Utilisateur u = (Utilisateur) session.getAttribute("utilisateur");
        if (!u.getRole().equals("chef_de_service") && !u.getRole().equals("Administrateur")) {
            return "redirect:/";
        }
        Stagiaire stagiaire = stagiaireRepository.findById(id).orElse(null);
        if (stagiaire != null) {
            stagiaire.setPresenteChefService(!stagiaire.isPresenteChefService());
            stagiaireRepository.save(stagiaire);
        }
        return "redirect:/details/" + id;
    }

    @GetMapping("/rechercher")
    public String rechercher(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String ecoleOrigine,
            @RequestParam(required = false) String typeStage,
            @RequestParam(required = false) String serviceDivision,
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            Model model, HttpSession session) {

        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }

        List<Stagiaire> resultats;

        if (nom != null && !nom.isEmpty()) {
            resultats = stagiaireRepository.findByNomContainingIgnoreCase(nom);
        } else if (prenom != null && !prenom.isEmpty()) {
            resultats = stagiaireRepository.findByPrenomContainingIgnoreCase(prenom);
        } else if (ecoleOrigine != null && !ecoleOrigine.isEmpty()) {
            resultats = stagiaireRepository.findByEcoleOrigineContainingIgnoreCase(ecoleOrigine);
        } else if (typeStage != null && !typeStage.isEmpty()) {
            resultats = stagiaireRepository.findByTypeStage(typeStage);
        } else if (serviceDivision != null && !serviceDivision.isEmpty()) {
            resultats = stagiaireRepository.findByServiceDivision(serviceDivision);
        } else if (dateDebut != null && !dateDebut.isEmpty() && dateFin != null && !dateFin.isEmpty()) {
            resultats = stagiaireRepository.findByDateDebutGreaterThanEqualAndDateFinLessThanEqual(dateDebut, dateFin);
        } else if (dateDebut != null && !dateDebut.isEmpty()) {
            resultats = stagiaireRepository.findByDateDebutGreaterThanEqual(dateDebut);
        } else if (dateFin != null && !dateFin.isEmpty()) {
            resultats = stagiaireRepository.findByDateFinLessThanEqual(dateFin);
        } else {
            resultats = stagiaireRepository.findAll();
        }

        model.addAttribute("stagiaires", resultats);
        return "liste_stagiaires";
    }
}