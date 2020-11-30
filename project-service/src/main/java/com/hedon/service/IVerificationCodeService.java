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
     * 发送验证码
     *
     * @param username  用户名：手机或邮箱
     */
    void sendRegisterCode(String username);
}
