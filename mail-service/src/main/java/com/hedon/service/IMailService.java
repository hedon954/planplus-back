package com.hedon.service;

/**
 * 邮件发送服务
 *
 * @author Hedon Wang
 * @create 2020-11-26 18:54
 */
public interface IMailService {

    /**
     * 发送文本邮件
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    void sendSimpleMail(String to, String subject, String content);

    /**
     * 发送HTML邮件
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param to 收件人
     * @param subject 主题
     * @param content 内容
     */
    void sendHtmlMail(String to, String subject, String content);


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
    void sendAttachmentsMail(String to, String subject, String content, String filePath);
}
