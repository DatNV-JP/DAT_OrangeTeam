package com.orange.services.impl;

import com.orange.utils.JsonUtils;
import com.orange.domain.model.MailInfo;
import com.orange.services.IMaillerService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaillerServiceImpl implements IMaillerService {
    private final JavaMailSender sender;
    private static final Logger logger = LoggerFactory.getLogger(MaillerServiceImpl.class);
    @Override
    public void send(MailInfo mail) throws MessagingException {
        logger.info(JsonUtils.toJson(mail));
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
        helper.setFrom("SanvadioShop <sanvadio.buzz@gmail.com>");
        helper.setTo(mail.getTo());
        helper.setSubject(mail.getSubject());
        helper.setText(mail.getBody(), true);
        helper.setReplyTo(mail.getFrom());

        String[] cc = mail.getCc();
        if (cc != null && cc.length > 0) {
            helper.setCc(cc);
        }
        String[] bcc = mail.getBcc();
        if (bcc != null && bcc.length > 0) {
            helper.setBcc(bcc);
        }
        String[] attachments = mail.getAttachments();
        if (attachments != null && attachments.length > 0) {
            for (String path : attachments) {
                File file = new File(path);
                helper.addAttachment(file.getName(), file);
            }
        }
        sender.send(message);
    }

    @Override
    public void send(String to, String subject, String body) throws MessagingException {
        this.send(new MailInfo(to, subject, body));
    }
    List<MailInfo> queue = new ArrayList<>();

    @Override
    public void queue(MailInfo mail) {
        queue.add(mail);
    }

    @Override
    public void queue(String to, String subject, String body) {
        queue(new MailInfo(to, subject, body));
    }
    @Scheduled(fixedDelay = 10000)
    public void run() {
        while (!queue.isEmpty()) {
            System.out.println("Start.....");
            MailInfo mail = queue.remove(0);
//            try {
//                send(mail);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }
}
