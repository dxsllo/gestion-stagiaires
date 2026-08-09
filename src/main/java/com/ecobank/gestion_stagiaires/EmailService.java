package com.ecobank.gestion_stagiaires;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void envoyerOtp(String destinataire, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire);
        message.setSubject("Code de vérification - ECOBANK Stagiaires");
        message.setText("Votre code de vérification est : " + otp + "\n\nCe code expire dans 5 minutes.");
        mailSender.send(message);
    }
}