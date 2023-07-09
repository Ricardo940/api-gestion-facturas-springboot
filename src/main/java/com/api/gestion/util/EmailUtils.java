package com.api.gestion.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text, List<String> list){

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom("capitaladelanto3@gmail.com");
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(text);

        if(list != null && list.size() > 0){
            mailMessage.setCc(list.toArray(new String[0]));
        }

        javaMailSender.send(mailMessage);
    }

    public void forgotPassword(String to, String subject, String password) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom("capitaladelanto3@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);

        String htmlMessage = "<p><b>Sus detalle de inicio de sesión para el sistema de facturas</b> <br> <b>Email : </b>"
                + to + "<br> <b>Password : </b>"
                + password + "<br> <a href='/'>"
                + "</p>";

        message.setContent(htmlMessage, "text/html");
        javaMailSender.send(message);
    }


}
