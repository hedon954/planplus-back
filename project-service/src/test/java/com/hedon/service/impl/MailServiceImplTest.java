package com.hedon.service.impl;

import com.hedon.ProjectApplication;
import com.hedon.service.IMailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


/**
 * 邮件发送测试类
 *
 * @author Hedon Wang
 * @create 2020-11-26 19:09
 */
@SpringBootTest(classes = {ProjectApplication.class})
public class MailServiceImplTest {

    @Autowired
    IMailService mailService;

    @Test
    void sendSimpleMail() {
        mailService.sendSimpleMail("171725713@qq.com","普通","内容");
    }

    @Test
    void sendHtmlMail() {
        mailService.sendHtmlMail("171725713@qq.com","HTML","<h1>内容</h1><h5>hhh</h5>");
    }

    @Test
    void sendAttachmentsMail() {
        mailService.sendAttachmentsMail("171725713@qq.com","附件","内容","/Volumes/Extreme SSD/竞赛/2020百度智能小程序/初选赛/系统开发/后端/dida-manager/project-service/src/test/java/com/hedon/service/impl/MailServiceImplTest.java");
    }
}