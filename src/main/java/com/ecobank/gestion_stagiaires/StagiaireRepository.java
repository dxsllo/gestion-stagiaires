package com.ecobank.gestion_stagiaires;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface StagiaireRepository extends JpaRepository<Stagiaire, Long> {
    Stagiaire findByEmail(String email);
    List<Stagiaire> findByNomContainingIgnoreCase(String nom);
    List<Stagiaire> findByPrenomContainingIgnoreCase(String prenom);
    List<Stagiaire> findByTypeStage(String typeStage);
    List<Stagiaire> findByServiceDivision(String serviceDivision);
    List<Stagiaire> findByEcoleOrigineContainingIgnoreCase(String ecole);
    List<Stagiaire> findByDateDebutGreaterThanEqual(String dateDebut);
    List<Stagiaire> findByDateFinLessThanEqual(String dateFin);
    List<Stagiaire> findByDateDebutGreaterThanEqualAndDateFinLessThanEqual(String dateDebut, String dateFin);
}
