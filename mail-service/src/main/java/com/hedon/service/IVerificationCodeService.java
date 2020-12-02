package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.VerificationCode;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-11-30
 */
public interface IVerificationCodeService extends IService<VerificationCode> {

    /**
     * 发送注册验证码
     *
     * @author Jiahan Wang
     * @create 2020.11.30
     * @param username  用户名：手机或邮箱
     */
    void sendRegisterCode(String username);

    /**
     * 发送找回密码验证码
     *
     * @author Jiahan Wang
     * @create 2020.11.30
     * @param username
     */
    void sendGetPasswordBackCode(String username);
}
