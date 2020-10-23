package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.DidaUser;

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
}
