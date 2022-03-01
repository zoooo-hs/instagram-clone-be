package com.zoooohs.instagramclone.domain.mail.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.zoooohs.instagramclone.configuration.MailSenderConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.Message;
import javax.mail.MessagingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MailSenderConfiguration.class)
public class MailServiceTest {

    /**
     * 메일 보내기 유닛테스트는 https://github.com/dimitrisli/SpringEmailTest/blob/master/src/test/java/com/dimitrisli/springEmailTest/EmailTest.java 를 참고했습니다.
     * Green Mail을 통해 SMTP 메일 서버를 스탠드얼론으로 띄워 메일 전송 테스트가 가능합니다.
     */

    MailService mailService;

    @Autowired
    JavaMailSenderImpl mailSender;
    GreenMail greenMail;

    @BeforeEach
    public void setUp() {

        greenMail = new GreenMail(new ServerSetup[] {
                new ServerSetup(3025, "127.0.0.1", ServerSetup.PROTOCOL_SMTP)
        });

        greenMail.start();
        greenMail.setUser("a", "aa");

        mailSender.setPort(3025);
        mailSender.setHost("localhost");
        mailSender.setUsername("a");
        mailSender.setPassword("aa");

        mailService = new MailServiceImpl(mailSender);
    }

    @AfterEach
    public void tearDown() {
        greenMail.stop();
    }

    @DisplayName("Mail Sender로 메일 보내기 테스트")
    @Test
    public void sendMailTest() throws MessagingException {
        String from = "test-from@test.com";
        String to = "test-to@test.com";
        String subject = "message subject";
        String text = "some text";

        mailService.send(to, subject, text);

        assertTrue(greenMail.waitForIncomingEmail(1));
        Message[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals(subject, messages[0].getSubject());
        assertEquals(text, GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", ""));
    }


}
