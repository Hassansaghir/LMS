package com.lms.emailservice.service.imp;

import com.lms.emailservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class DefaultEmailService implements EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject("Library Notification");
        mailMessage.setText(message);
        mailSender.send(mailMessage);
        log.info("Email sent successfully to: " + toEmail);
    }
}
