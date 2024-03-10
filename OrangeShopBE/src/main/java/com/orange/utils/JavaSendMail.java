package com.orange.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class JavaSendMail {
    private static JavaMailSender emailSender = null;

    @Autowired
    public JavaSendMail(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public static void sendEmail(String sendTo,String subJect, String text) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(sendTo);
        helper.setSubject(subJect);
        helper.setText(text);
        emailSender.send(message);
    }
}
