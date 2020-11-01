package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hedon.dto.DidaUserDTO;
import common.entity.DidaUser;
import common.vo.common.ResponseBean;
import org.springframework.stereotype.Service;

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

//    /**
//     * 修改用户密码
//     * @param userId
//     * @param old_psw
//     * @param new_psw
//     * @return
//     */
//    void updatePassword(Integer userId,String old_psw,String new_psw);
}
