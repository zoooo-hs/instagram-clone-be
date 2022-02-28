package com.zoooohs.instagramclone.domain.service;

public interface MailService {
    void send(String to, String subject, String text);
}
