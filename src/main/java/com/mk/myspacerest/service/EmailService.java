package com.mk.myspacerest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    private final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendSimpleMessage(String to, String subject, String text) {
        logger.info("Start sendSimpleMessage to: {}, subject: {}, text: {}", to, subject, text);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("from@example.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        //emailSender.send(message);
    }
}