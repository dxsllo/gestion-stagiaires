package com.ecobank.gestion_stagiaires;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UtilisateurRepository UtilisateurRepository;

    @Override
    public void run(String... args) {
        if (UtilisateurRepository.count() == 0) {
            Utilisateur admin = new Utilisateur();
            admin.setNom("Admin");
            admin.setIdentifiant("admin");
            admin.setMotDePasse("12345");
            admin.setRole("Administrateur");
            admin.setEmail("kebegift12@gmail.com");
            admin.setTelephone("0749980642");
            UtilisateurRepository.save(admin);
            System.out.println("Compte admin créé avec succès !");

            Utilisateur drh = new Utilisateur();
            drh.setNom("DRH");
            drh.setIdentifiant("drh");
            drh.setMotDePasse("drh12345");
            drh.setRole("DRH");
            drh.setEmail("kebegift12@gmail.com");
            drh.setTelephone("0749980642");
            UtilisateurRepository.save(drh);

            Utilisateur chef = new Utilisateur();
            chef.setNom("Chef de Service");
            chef.setIdentifiant("Chief");
            chef.setMotDePasse("chief123");
            chef.setRole("chef_de_service");
            chef.setEmail("kebegift12.com");
            chef.setTelephone("0749980642");
            UtilisateurRepository.save(chef);
        }
    }
}