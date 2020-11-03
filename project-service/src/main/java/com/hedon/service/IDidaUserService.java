package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.DidaUser;
import common.vo.common.ResponseBean;
import common.vo.common.UserBaiduInfo;
import org.springframework.stereotype.Service;
import common.vo.request.DidaUserRequestVo;
import common.vo.response.DidaUserResponseVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
public interface IDidaUserService extends IService<DidaUser> {

    /**
     * 根据ID查询用户信息
     *
     * @author hedon
     * @create 2020.10.23
     * @param userId
     * @return
     */
    DidaUser getUserById(Integer userId);

    /**
     * 根据修改用户信息
     * @author Ruolin
     * @create 2020.10.29
     * @param didaUser
     */
    void updateUserInfoById(DidaUser didaUser);


    /**
     * 登录
     *
     * @author Jiahan Wang
     * @create 2020.11.1
     * @param phoneNumber  手机号
     * @param password     密码
     * @return
     */
    DidaUserResponseVo login(String phoneNumber, String password);

    /**
     * @author Ruolin
     * @create 2020.11.2
     * @param userId    用户id
     * @param oldPwd    旧密码
     * @param newPwd    新密码
     */
    void updatePassword(Integer userId,String oldPwd,String newPwd);

    /**
     * 存储用户百度信息
     * @param userId        用户ID
     * @param userBaiduInfo 里面有 openId 和 sessionKey
     */
    void saveUserBaiduInfo(Integer userId, UserBaiduInfo userBaiduInfo);
}
