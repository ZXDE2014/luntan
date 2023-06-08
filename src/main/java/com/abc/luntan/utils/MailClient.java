package com.abc.luntan.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
@Slf4j
public class MailClient {
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;


    public void sendMail(String to, String subject, String content) {
        try {
            //MimeMessage用于封装邮件相关信息
            MimeMessage message = javaMailSender.createMimeMessage();
            //需要一个邮件帮助器，负责构建MimeMessage对象
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            //支持HTML文本
            helper.setText(content, true);
            //发送邮件都有JavaMailSender来做
            javaMailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            log.error("发送邮件失败：" + e.getMessage());
        }
    }

}
