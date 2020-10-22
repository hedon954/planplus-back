package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.User;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-16
 */
public interface IUserService extends IService<User> {
    List<User> getUsers();
}
