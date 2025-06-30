package org.email;

import java.awt.*;
import java.net.URL;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {

    public static void main(String[] args) {

        // כתובת הנמען
        String to = args[0];

        // כתובת שולח (Outlook)
        String from = "notreply.lilach@gmail.com";

        // הגדרות שרת ה-SMTP של Outlook
        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // השרת של Gmail
        properties.put("mail.smtp.port", "587");            // פורט מאובטח עם TLS
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.connectiontimeout", "10000");
        properties.put("mail.smtp.timeout", "10000");



        // יצירת session עם אימות משתמש
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("notreply.lilach@gmail.com", "shloqmwedlmhtiep");
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(args[2]);
            message.setText(args[1]);

            System.out.println("sending... to " + to + " | subject: " + args[2]);
            Transport.send(message);
            System.out.println("Sent message successfully....");

        } catch (MessagingException mex) {
            System.out.println("unsuccessfully sending: ");
            mex.printStackTrace();
        }
    }

    public static void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
