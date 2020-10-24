package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.DidaUser;
import common.vo.request.DidaUserRequestVo;

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
     * 根据requestVo修改用户信息
     *
     * @author yang jie
     * @create 2020.10.24
     * @param requestVo 封装的用户信息
     */
    void updateUserByVo(DidaUserRequestVo requestVo);

}
