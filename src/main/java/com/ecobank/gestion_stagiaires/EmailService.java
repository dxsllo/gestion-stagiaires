package com.ecobank.gestion_stagiaires;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class EmailService {

    private final String API_KEY = "re_8Nn8iMrB_48tXz7Rec33UkZwe1jXMnqg6";

    public void envoyerOtp(String destinataire, String otp) {
        try {
            String json = "{" +
                    "\"from\": \"ECOBANK Stagiaires <onboarding@resend.dev>\"," +
                    "\"to\": [\"" + destinataire + "\"]," +
                    "\"subject\": \"Code de vérification - ECOBANK\"," +
                    "\"text\": \"Votre code de vérification est : " + otp + ". Ce code expire dans 5 minutes.\"" +
                    "}";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + API_KEY)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Erreur envoi email: " + e.getMessage());
        }
    }
}