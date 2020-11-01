package com.hedon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.service.IDidaUserService;
import common.code.ResultCode;
import common.entity.DidaUser;
import common.exception.ServiceException;
import common.mapper.DidaUserMapper;
import common.vo.request.DidaUserRequestVo;
import common.vo.response.DidaUserResponseVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 根据requestVo修改用户信息
     *
     * @author yang jie
     * @create 2020.10.24
     * @param requestVo
     */
    @Override
    public void updateUserByVo(DidaUserRequestVo requestVo) {
        if(requestVo.getUserId() == null) {
            throw new ServiceException(ResultCode.EMPTY_USER_ID);
        }
        didaUserMapper.updateUserByVo(requestVo);
    }

    /**
     * 登录
     *
     * @author Jiahan Wang
     * @create 2020.11.1
     * @param phoneNumber  手机号
     * @param password     密码
     * @return
     */
    @Override
    public DidaUserResponseVo login(String phoneNumber, String password) {
        //先判断用户是否存在
        QueryWrapper<DidaUser> phoneQuery = new QueryWrapper<>();
        phoneQuery.eq("user_phone",phoneNumber);
        List<DidaUser> didaUsers = didaUserMapper.selectList(phoneQuery);
        //查不到，说明用户不存在
        if (didaUsers.size() < 1){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        //查到了，判断密码是否正确
        DidaUser didaUser = didaUsers.get(0);
        if (!StringUtils.equals(didaUser.getUserPassword(),password)){
            throw new ServiceException(ResultCode.ERROR_PASSWORD);
        }
        DidaUserResponseVo didaUserResponseVo = new DidaUserResponseVo(didaUser);
        return didaUserResponseVo;
    }
}
