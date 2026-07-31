package com.ecobank.gestion_stagiaires;

import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;

    @Entity
    public class Stagiaire{
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)//auto-incrémentation de l'ID
        private Long id;
        private String nom;
        private String prenom;
        private String telephone;
        @Column(unique = true)
        private String email;
        private String ecoleOrigine;
        private String dateDebut;
        private String dateFin;
        private String typeStage;
        private String serviceDivision;
        private String tachesDonnees;
        private int nbTotalTaches;
        private int nbTachesRealisees;
        private float note;
        private boolean memoireRedige;
        private boolean presenteDRH;
        private boolean presenteChefService;

        //Getters-setters
        public Long getId() {
            return id;
        }
        public void setId(Long id) {
            this.id = id;
        }

        public String getNom() {
            return nom;
        }
        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }
        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getTelephone() {
            return telephone;
        }
        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getEmail() {
            return email;
        }
        public void setEmail(String email) {
            this.email=email;
        }

        public String getEcoleOrigine() {
            return ecoleOrigine;
        }
        public void setEcoleOrigine(String ecoleOrigine) {
            this.ecoleOrigine = ecoleOrigine;
        }

        public String getDateDebut() {
            return dateDebut;
        }

        public void setDateDebut(String dateDebut) {
            this.dateDebut = dateDebut;
        }

        public String getDateFin() {
            return dateFin;
        }

        public void setDateFin(String dateFin) {
            this.dateFin = dateFin;
        }

        public String getTypeStage() {
            return typeStage;
        }

        public void setTypeStage(String typeStage) {
            this.typeStage = typeStage;
        }

        public String getServiceDivision() {
            return serviceDivision;
        }

        public void setServiceDivision(String serviceDivision) {
            this.serviceDivision = serviceDivision;
        }

        public boolean isMemoireRedige() {
            return memoireRedige;
        }
        public void setMemoireRedige(boolean memoireRedige) {
            this.memoireRedige = memoireRedige;
        }

        public boolean isPresenteDRH() {
            return presenteDRH;
        }
        public void setPresenteDRH(boolean presenteDRH) {
            this.presenteDRH = presenteDRH;
        }

        public boolean isPresenteChefService() {
            return presenteChefService;
        }
        public void setPresenteChefService(boolean presenteChefService) {
            this.presenteChefService = presenteChefService;
        }

        public String getTachesDonnees() {
            return tachesDonnees;
        }
        public void setTachesDonnees(String tachesDonnees) {
            this.tachesDonnees = tachesDonnees;
        }

        public int getNbTotalTaches() {
            return nbTotalTaches;
        }

        public void setNbTotalTaches(int nbTotalTaches) {
            this.nbTotalTaches = nbTotalTaches;
        }

        public int getNbTachesRealisees() {
            return nbTachesRealisees;
        }

        public void setNbTachesRealisees(int nbTachesRealisees) {
            this.nbTachesRealisees = nbTachesRealisees;
        }

        public float getNote() {
            return note;
        }

        public void setNote(float note) {
            this.note = note;
        }
    }


