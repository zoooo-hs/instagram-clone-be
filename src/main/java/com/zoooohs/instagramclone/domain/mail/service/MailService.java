package com.zoooohs.instagramclone.domain.mail.service;

public interface MailService {
    void send(String to, String subject, String text);
}
