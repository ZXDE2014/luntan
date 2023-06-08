package com.abc.luntan.utils;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@SpringBootTest
public class JavaMailTest {
    @Autowired
    private MailClient mailClient;

    @Test
    public void sendMail() throws MessagingException {
        mailClient.sendMail("zhangxuezhang66@gmail.com", "测试邮件", "测试邮件内容");
    }

    public static void main(String[] args) throws MessagingException {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.auth", "true");//认证
        properties.setProperty("mail.transport.protocol", "smtp");
        Session session = Session.getInstance(properties);
        session.setDebug(true);
        Message message = new MimeMessage(session);
        message.setText("Hello World");
        message.setFrom(new InternetAddress("1491310526@qq.com"));

        Transport transport = session.getTransport();
        transport.connect("smtp.qq.com", "1491310526@qq.com", "eljvwecfykuchdbj");//此次XXXXXX为授权码
        transport.sendMessage(message, new Address[]{new InternetAddress("zhangxuezhang66@gmail.com")});
        transport.close();
    }
}