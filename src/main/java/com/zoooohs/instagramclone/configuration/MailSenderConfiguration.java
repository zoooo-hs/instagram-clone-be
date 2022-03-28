package com.zoooohs.instagramclone.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailSenderConfiguration {

    @Bean
    public JavaMailSender getMailSender(
            @Value("${instagram-clone.mail.username}") String username,
            @Value("${instagram-clone.mail.password}") String password,
            @Value("#{new Boolean('${instagram-clone.mail.activation}')}") boolean activation
    ) {
        if (!activation) {
            return new JavaMailSenderImpl();
        }
        // TODO: gmail 말고 다양한 벤더 오픈
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.debug", "false");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(password);
        mailSender.setDefaultEncoding("UTF-8");
        mailSender.setJavaMailProperties(properties);

        return mailSender;
    }
}
