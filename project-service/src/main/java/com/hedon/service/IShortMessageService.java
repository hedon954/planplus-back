package com.hedon.service;

import common.vo.common.ResponseBean;

/**
 * 短信服务
 *
 * @author Hedon Wang
 * @create 2020-11-29 15:13
 */
public interface IShortMessageService {


    /**
     * 发送验证码
     */
    ResponseBean sendCode(String phoneNumber);

}
