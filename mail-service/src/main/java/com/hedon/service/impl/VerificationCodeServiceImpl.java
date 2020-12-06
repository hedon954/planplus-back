package com.hedon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.feign.NotificationFeignService;
import com.hedon.service.IMailService;
import com.hedon.service.IVerificationCodeService;
import common.code.ResultCode;
import common.entity.DidaUser;
import common.entity.VerificationCode;
import common.exception.ServiceException;
import common.mapper.DidaUserMapper;
import common.mapper.VerificationCodeMapper;
import common.util.EmailFormatCheckUtils;
import common.util.PhoneFormatCheckUtils;
import common.vo.common.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-11-30
 */
@Service
@Slf4j
public class VerificationCodeServiceImpl extends ServiceImpl<VerificationCodeMapper, VerificationCode> implements IVerificationCodeService {

    private static final String WAY_REGISTER ="register";
    private static final String WAY_GET_PASSWORD_BACK ="getPasswordBack";

    @Autowired
    private VerificationCodeMapper verificationCodeMapper;

    @Autowired
    private IMailService mailService;

    @Autowired
    private DidaUserMapper didaUserMapper;

    @Autowired
    NotificationFeignService notificationFeignService;


    /**
     * 发送验证码
     *
     * @param username  用户名：手机或邮箱
     */
    @Override
    public void sendRegisterCode(String username) {
        //检查用户是否已存在
        DidaUser user = didaUserMapper.getUserByPhoneOrEmail(username);
        if (user != null){
            throw new ServiceException(ResultCode.USER_EXIST);
        }
        //随机生成验证码
        String code = String.valueOf(RandomUtils.nextInt(0,899999) + 100000);
        //先检查是否有记录
        QueryWrapper<VerificationCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code_username",username);
        VerificationCode verificationCode = verificationCodeMapper.selectOne(queryWrapper);
        if (verificationCode != null){
            verificationCode.setCodeNumber(code);
            verificationCode.setIsActive(1);
            //如果已经有记录，那就不需要检查 username 是否合法了
            sendCode(verificationCode,WAY_REGISTER);
        }else{
            //如果没有记录
            verificationCode = new VerificationCode();
            verificationCode.setCodeUsername(username);
            verificationCode.setCodeNumber(code);
            verificationCode.setIsActive(1);
            checkAndSendCode(verificationCode,WAY_REGISTER);
        }
        //调用通知模块，将code扔入死信队列
        ResponseBean responseBean = notificationFeignService.sendCode(verificationCode);
        if (responseBean.getCode() != 1000L){
            //如果发送消息不成功，抛出异常
            throw new ServiceException(ResultCode.GET_VERIFICATION_CODE_FAILED);
        }

    }

    /**
     * 发送找回密码验证码
     *
     * @author Jiahan Wang
     * @create 2020.11.30
     * @param username
     */
    @Override
    public void sendGetPasswordBackCode(String username) {
        //检查用户是否已存在
        DidaUser user = didaUserMapper.getUserByPhoneOrEmail(username);
        if (user == null){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        //随机生成验证码
        String code = String.valueOf(RandomUtils.nextInt(0,899999) + 100000);
        //先检查是否有记录
        QueryWrapper<VerificationCode> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code_username",username);
        VerificationCode verificationCode = verificationCodeMapper.selectOne(queryWrapper);
        if (verificationCode != null){
            verificationCode.setCodeNumber(code);
            verificationCode.setIsActive(1);
            //如果已经有记录，那就不需要检查 username 是否合法了
            sendCode(verificationCode,WAY_GET_PASSWORD_BACK);
        }else{
            //如果没有记录
            verificationCode = new VerificationCode();
            verificationCode.setCodeUsername(username);
            verificationCode.setCodeNumber(code);
            verificationCode.setIsActive(1);
            checkAndSendCode(verificationCode,WAY_GET_PASSWORD_BACK);
        }
        //调用通知模块，将code扔入死信队列
        ResponseBean responseBean = notificationFeignService.sendCode(verificationCode);
        if (responseBean.getCode() != 1000L){
            //如果发送消息不成功，抛出异常
            throw new ServiceException(ResultCode.GET_VERIFICATION_CODE_FAILED);
        }
    }

    /**
     * 发送验证码 —— 针对已有记录的
     *
     * @param verificationCode
     * @param way 用途
     */
    public void sendCode(VerificationCode verificationCode, String way){
        String username = verificationCode.getCodeUsername();
        //判断是邮箱还是手机
        if (username.contains("@")){
            //发送邮箱验证码
            try{
                mailService.sendHtmlMail(verificationCode.getCodeUsername(),"PlanPlus",formatCodeContent(verificationCode.getCodeNumber(),way));
                //更新记录
                verificationCodeMapper.updateById(verificationCode);
            }catch (Exception e){
                throw new ServiceException("发送验证码失败，请检查邮箱是否正确",ResultCode.FEEDBACK_FAILED);
            }
        }else{
            //发送手机验证码 —— 先不支持
            throw new ServiceException(ResultCode.GET_VERIFICATION_CODE_FAILED);
        }
    }

    /**
     * 检查格式并发送验证码 —— 针对没有记录的
     *
     * @param verificationCode
     * @param way 用途
     */
    public void checkAndSendCode(VerificationCode verificationCode,String way){
        String username = verificationCode.getCodeUsername();
        //判断是邮箱还是手机
        if (username.contains("@")){
            //判断邮箱格式是否正确
            if (!EmailFormatCheckUtils.isEmailLegal(username)){
                throw new ServiceException(ResultCode.EMAIL_FORMAT_ERROR);
            }
            //发送邮箱验证码
            try{
                mailService.sendHtmlMail(verificationCode.getCodeUsername(),"PlanPlus",formatCodeContent(verificationCode.getCodeNumber(),way));
                //插入记录
                verificationCodeMapper.insert(verificationCode);
            }catch (Exception e){
                throw new ServiceException("发送验证码失败，请检查邮箱是否正确",ResultCode.FEEDBACK_FAILED);
            }
        }else{
            //判断手机格式是否正确
            if (!PhoneFormatCheckUtils.isPhoneLegal(username)){
                throw new ServiceException(ResultCode.PHONE_FORMAT_ERROR);
            }
            //发送手机验证码 —— 先不支持
            throw new ServiceException(ResultCode.GET_VERIFICATION_CODE_FAILED);
        }
    }

    /**
     * 将验证码信息进行格式化
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param code
     * @param way 用途
     * @return
     */
    private String formatCodeContent(String code, String way) {
        StringBuilder stringBuilder = new StringBuilder();
        if (way.equals(WAY_REGISTER)){
            //注册
            stringBuilder
                    .append("<h1>【PlanPlus】").append("</h1>")
                    .append("<HR style=\"border:1 dashed #987cb9\" width=\"100%\" SIZE=1>")
                    .append("<p>您正在注册成为 PlanPlus 的一员！验证码为：").append(code).append("</p>")
                    .append("<p>请在").append("五分钟").append("内使用！</p>");

        }else if (way.equals(WAY_GET_PASSWORD_BACK)){
            //找回密码
            stringBuilder
                    .append("<h1>【PlanPlus】").append("</h1>")
                    .append("<HR style=\"border:1 dashed #987cb9\" width=\"100%\" SIZE=1>")
                    .append("<p>您正在找回密码！验证码为：").append(code).append("</p>")
                    .append("<p>请在").append("五分钟").append("内使用！</p>");
        }

        return stringBuilder.toString();
    }
}
