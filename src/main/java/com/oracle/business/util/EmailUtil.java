package com.oracle.business.util;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    public static void sendEmail(String toAddress, String subject, String messageBody) throws MessagingException {
        String host = "smtp.gmail.com";
        String port = "587";
        String username = "saniaa.khan1221@gmail.com";
        String password = "kpaknurctgcoiltk";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(username));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
        msg.setSubject(subject);
        msg.setText(messageBody);

        Transport.send(msg);
    }
}


