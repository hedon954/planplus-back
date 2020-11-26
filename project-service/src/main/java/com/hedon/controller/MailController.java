package com.hedon.controller;

import com.hedon.service.IMailService;
import common.code.ResultCode;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import common.vo.request.FeedbackRequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * 邮件控制器
 *
 * @author Hedon Wang
 * @create 2020-11-26 19:21
 */
@RestController
@RequestMapping("/project/mail")
public class MailController {

    @Autowired
    IMailService mailService;

    @Value("${spring.mail.to}")
    private String to;

    /**
     * 发送反馈信息
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param userId             用户ID
     * @param feedbackRequestVo  反馈信息
     * @return
     */
    @PostMapping("/feedback")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseBean sendFeedbackEmail(
            @AuthenticationPrincipal(expression = "#this.userId") Integer userId,
            @RequestBody FeedbackRequestVo feedbackRequestVo){
        if (feedbackRequestVo == null){
            return ResponseBean.fail(ResultCode.FEEDBACK_FAILED);
        }
        try{
            String feedback = formatFeedbackContent(userId,feedbackRequestVo);
            mailService.sendHtmlMail(to,"PlanPlus用户反馈邮件",feedback);
            return ResponseBean.success();
        }catch (ServiceException e){
            return e.getFailResponse();
        }
    }

    /**
     * 将反馈信息进行格式化
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param userId
     * @param feedbackRequestVo
     * @return
     */
    private String formatFeedbackContent(Integer userId, FeedbackRequestVo feedbackRequestVo) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder
                .append("<h1>用户ID：").append(userId).append("</h1>")
                .append("<HR style=\"border:1 dashed #987cb9\" width=\"100%\" SIZE=1>")
                .append("<h5>").append("反馈内容：").append("</h5>")
                .append("<h5>").append(feedbackRequestVo.getContent()).append("</h5>")
                .append("<HR style=\"border:1 dashed #987cb9\" width=\"100%\" SIZE=1>")
                .append("用户邮箱：").append(feedbackRequestVo.getEmail()).append("")
                .append("<HR style=\"border:1 dashed #987cb9\" width=\"100%\" SIZE=1>")
                .append("反馈时间：").append(LocalDateTime.now().format(dtf));
        return stringBuilder.toString();
    }
}
