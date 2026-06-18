package com.eventhub.notifications_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * Envoi d'un email simple (texte brut)
     */
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        // Optionnel : adresse d'expéditeur (si non définie dans Gmail)
        // message.setFrom("TON_EMAIL@gmail.com");

        mailSender.send(message);
    }
}
