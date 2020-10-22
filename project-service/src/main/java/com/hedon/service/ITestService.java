package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.Test;
import common.entity.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-16
 */
public interface ITestService extends IService<Test> {

    Test getById(Integer id);

    User getUserById(Integer id);

    int updateUser(User user);
}
