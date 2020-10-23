package com.hedon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.service.IDidaUserService;
import common.code.ResultCode;
import common.entity.DidaUser;
import common.exception.ServiceException;
import common.mapper.DidaUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@Service
public class DidaUserServiceImpl extends ServiceImpl<DidaUserMapper, DidaUser> implements IDidaUserService {

    @Autowired
    DidaUserMapper didaUserMapper;

    /**
     * 根据ID查询用户信息
     *
     * @author hedon
     * @create 2020.10.23
     * @param userId
     * @return
     */
    @Override
    public DidaUser getUserById(Integer userId) {
        DidaUser didaUser = didaUserMapper.selectById(userId);
        //若用户为空在，则抛出异常->用户不存在
        if (didaUser == null){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        return didaUser;
    }
}
