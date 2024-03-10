package com.orange.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailInfo {
    String from;
    String to;
    String[] cc;
    String[] bcc;
    String subject;
    String body;
    String[] attachments;
    public MailInfo(String to, String subject, String body) {
        super();
        this.from = "Sanvadio Shop <sanvadio.buzz@gmail.com>";
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}
