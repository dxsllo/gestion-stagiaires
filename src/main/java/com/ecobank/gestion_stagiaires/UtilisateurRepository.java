package com.ecobank.gestion_stagiaires;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Utilisateur findByIdentifiantAndMotDePasse(String identifiant, String motDePasse);
}
