package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.DidaUser;
import common.vo.common.ResponseBean;
import common.vo.common.UserBaiduInfo;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import common.vo.request.DidaUserRequestVo;
import common.vo.response.DidaUserResponseVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
     * [已废弃]
     *
     * 登录
     *
     * @author Jiahan Wang
     * @create 2020.11.1
     * @param phoneNumber  手机号
     * @param password     密码
     * @return
     */
    @Deprecated
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


    /**
     * 上传头像
     * @param userId 用户id
     * @param file 头像文件
     * @throws IOException
     */
    void uploadAvatar(Integer userId, MultipartFile file) throws IOException;

    /**
     * 获取头像
     * @param userId 用户id
     * @return
     */
    Resource loadAvatar(Integer userId);

    /**
     * 通过手机号和密码进行注册
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param username
     * @param password
     * @param code
     */
    void register(String username, String password, String code);

    /**
     * 找回密码
     *
     * @author Jiahan Wang
     * @create 2020.11.30
     * @param username
     * @param password
     * @param code
     */
    void getPasswordBack(String username, String password, String code);

    /**
     * 根据 unionId 查询用户信息
     *
     * @author Jiahan Wang
     * @create 2020.12.6
     * @param userUnionId
     * @param userOpenId
     * @param userSessionKey
     * @return
     */
    DidaUser selectUserByUnionId(String userOpenId, String userSessionKey, String userUnionId);

    /**
     * 在登录的时候根据 UnionId 获取用户信息，没有的话就登录失败，不进行自动注册
     *
     * @param userUnionId
     * @return
     */
    DidaUser getUserByUnionIdWhenLogin(String userUnionId);
}
