package com.zoooohs.instagramclone.domain.mail.service;

import com.zoooohs.instagramclone.exception.ErrorCode;
import com.zoooohs.instagramclone.exception.ZooooException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    @Override
    public void send(String to, String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to, to, "UTF-8"));
            message.setSubject(subject);
            message.setText(text, "UTF-8", "html");

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ZooooException(ErrorCode.INTERNAL_ERROR);
        }
    }
}
