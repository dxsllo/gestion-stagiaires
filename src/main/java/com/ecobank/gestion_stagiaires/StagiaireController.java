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
    public String listeStagiaires(Model model, HttpSession session) {
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
    public String afficherModification(@PathVariable Long id, Model model, HttpSession session) {
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
    public String supprimerStagiaire(@PathVariable Long id, HttpSession session) {
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
        // Compteurs par sexe
        int nbMasculin = 0;
        int nbFeminin = 0;

// Compteurs par type de stage
        int nbObservation = 0;
        int nbPerfectionnement = 0;
        int nbFinEtudes = 0;
        int nbPreEmploi = 0;

        for (Stagiaire s : stagiaires) {
            // Compter par sexe
            if ("Masculin".equals(s.getSexe())) nbMasculin++;
            if ("Féminin".equals(s.getSexe())) nbFeminin++;

            // Compter par type de stage
            if ("Stage d'observation".equals(s.getTypeStage())) nbObservation++;
            if ("Stage de perfectionnement".equals(s.getTypeStage())) nbPerfectionnement++;
            if ("Stage de fin d'études".equals(s.getTypeStage())) nbFinEtudes++;
            if ("Stage pré-emploi".equals(s.getTypeStage())) nbPreEmploi++;
        }

        model.addAttribute("nbMasculin", nbMasculin);
        model.addAttribute("nbFeminin", nbFeminin);
        model.addAttribute("nbObservation", nbObservation);
        model.addAttribute("nbPerfectionnement", nbPerfectionnement);
        model.addAttribute("nbFinEtudes", nbFinEtudes);
        model.addAttribute("nbPreEmploi", nbPreEmploi);

        model.addAttribute("totalStagiaires", totalStagiaires);
        model.addAttribute("nbMemoiresRediges", nbMemoiresRediges);
        model.addAttribute("nbPresentesDRH", nbPresentesDRH);
        model.addAttribute("nbPresentesChef", nbPresentesChef);
        model.addAttribute("stagiaires", stagiaires);
        
        // Stats par service
        java.util.Map<String, Integer> statsParService = new java.util.LinkedHashMap<>();
        for (Stagiaire s : stagiaires) {
            if (s.getServiceDivision() != null && !s.getServiceDivision().isEmpty()) {
                statsParService.merge(s.getServiceDivision(), 1, Integer::sum);
            }
        }
        model.addAttribute("statsParService", statsParService);

// Stats par école
        java.util.Map<String, Integer> statsParEcole = new java.util.LinkedHashMap<>();
        for (Stagiaire s : stagiaires) {
            if (s.getEcoleOrigine() != null && !s.getEcoleOrigine().isEmpty()) {
                statsParEcole.merge(s.getEcoleOrigine(), 1, Integer::sum);
            }
        }
        model.addAttribute("statsParEcole", statsParEcole);

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
            @RequestParam(required = false) String sexe,
            @RequestParam(required = false) String themeStage,
            Model model, HttpSession session) {

        if (session.getAttribute("utilisateur") == null) {
            return "redirect:/connexion";
        }

        List<Stagiaire> resultats = stagiaireRepository.findAll();

        if (nom != null && !nom.isEmpty()) {
            resultats = resultats.stream()
                    .filter(s -> s.getNom().toLowerCase().contains(nom.toLowerCase()))
                    .toList();
        }
        if (prenom != null && !prenom.isEmpty()) {
            resultats = resultats.stream()
                    .filter(s -> s.getPrenom().toLowerCase().contains(prenom.toLowerCase()))
                    .toList();
        }
        if (sexe != null && !sexe.isEmpty()) {
            resultats = resultats.stream()
                    .filter(s -> sexe.equals(s.getSexe()))
                    .toList();
        }
        if (ecoleOrigine != null && !ecoleOrigine.isEmpty()) {
            resultats = resultats.stream()
                    .filter(s -> s.getEcoleOrigine().toLowerCase().contains(ecoleOrigine.toLowerCase()))
                    .toList();
        }
        if (typeStage != null && !typeStage.isEmpty()) {
            resultats = resultats.stream()
                    .filter(s -> typeStage.equals(s.getTypeStage()))
                    .toList();
        }
        if (serviceDivision != null && !serviceDivision.isEmpty()) {
            resultats = resultats.stream()
                    .filter(s -> serviceDivision.equals(s.getServiceDivision()))
                    .toList();
        }
        if (themeStage != null && !themeStage.isEmpty()) {
            resultats = resultats.stream()
                    .filter(s -> s.getThemeStage() != null && s.getThemeStage().toLowerCase().contains(themeStage.toLowerCase()))
                    .toList();
        }
        if (dateDebut != null && !dateDebut.isEmpty()) {
            resultats = resultats.stream()
                    .filter(s -> s.getDateDebut() != null && s.getDateDebut().compareTo(dateDebut) >= 0)
                    .toList();
        }
        if (dateFin != null && !dateFin.isEmpty()) {
            resultats = resultats.stream()
                    .filter(s -> s.getDateFin() != null && s.getDateFin().compareTo(dateFin) <= 0)
                    .toList();
        }

        model.addAttribute("stagiaires", resultats);
        return "liste_stagiaires";
    }
}