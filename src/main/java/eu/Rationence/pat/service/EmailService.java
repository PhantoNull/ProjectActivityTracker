package eu.Rationence.pat.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
        helper.setText(text +
                "<br><br><br><small>-Project Activity Tracking automatic email, do not reply</small><br>" +
                "<br><img src='https://www.rationence.eu/RationenceAssets/images/RationenceLogo3b.png' width=\"165\" height=\"18\">", true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("pat@rationence.eu");
        emailSender.send(message);
    }
}
