package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hedon.dto.DidaUserDTO;
import common.entity.DidaUser;
import common.vo.common.ResponseBean;
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
}
