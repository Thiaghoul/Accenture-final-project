package com.group5.taskFlow.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void whenSendSimpleMessage_thenMailSenderIsCalled() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Body";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@taskflow.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        // Act
        emailService.sendSimpleMessage(to, subject, text);

        // Assert
        verify(javaMailSender).send(message);
    }
}
