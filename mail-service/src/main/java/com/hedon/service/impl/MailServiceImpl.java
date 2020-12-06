package com.hedon.service.impl;

import com.hedon.service.IMailService;
import common.code.ResultCode;
import common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * 邮件服务实现类
 *
 * @author Hedon Wang
 * @create 2020-11-26 18:54
 */
@Service
@Slf4j
public class MailServiceImpl implements IMailService {

    /**
     * 邮件发送器
     */
    @Autowired
    private JavaMailSender javaMailSender;

    /**
     * 配置文件中我的qq邮箱
     */
    @Value("${spring.mail.from}")
    private String from;

    /**
     * 发送文本邮件
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendSimpleMail(String to, String subject, String content) {
        try {
            //信息体
            SimpleMailMessage message = new SimpleMailMessage();
            //邮件发送人
            message.setFrom(from);
            //邮件接收人
            message.setTo(to);
            //邮件主题
            message.setSubject(subject);
            //邮件内容
            message.setText(content);
            //发送邮件
            javaMailSender.send(message);
        }catch (Exception e){
            throw new ServiceException(ResultCode.FEEDBACK_FAILED);
        }
    }

    /**
     * 发送HTML邮件
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    @Override
    public void sendHtmlMail(String to, String subject, String content) {
        //信息封装体
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            //邮件发送帮助器
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            //邮件发送人
            helper.setFrom(from);
            //邮件接收人
            helper.setTo(to);
            //邮件主题
            message.setSubject(subject);
            //邮件内容和格式，第二个参数 true 表示支持 html
            helper.setText(content,true);
            //发送邮件
            javaMailSender.send(message);
            //日志信息
            log.info("邮件发送成功");
        }catch (MessagingException e){
            log.error("邮件发送错误",e);
            throw new ServiceException(ResultCode.FEEDBACK_FAILED);
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     * @param filePath 附件
     */
    @Override
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        //信息封装体
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message,true);
            helper.setFrom(from);
            helper.setTo(to);
            message.setSubject(subject);
            helper.setText(content,true);
            //附件
            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = filePath.substring(filePath.lastIndexOf(File.separator));
            helper.addAttachment(fileName,file);
            //发送邮件
            javaMailSender.send(message);
            log.info("邮件发送成功");
        }catch (MessagingException e){
            log.error("邮件发送错误",e);
            throw new ServiceException(ResultCode.FEEDBACK_FAILED);
        }
    }
}
