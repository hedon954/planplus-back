package com.hedon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hedon.service.DidaUserService;
import common.entity.DidaUser;
import common.mapper.DidaUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Hedon Wang
 * @create 2020-11-02 17:56
 */
@Service
public class DidaUserServiceImpl implements DidaUserService {

    @Autowired
    private DidaUserMapper didaUserMapper;

    /**
     * 根据手机号获取用户ID
     * @param phoneNumber
     * @return
     */
    @Override
    public Integer getUserIdByUserPhoneNumber(String phoneNumber) {
        QueryWrapper<DidaUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_phone",phoneNumber);
        List<DidaUser> didaUsers = didaUserMapper.selectList(queryWrapper);
        //如果差不到，就返回0
        if (didaUsers.size() < 1){
            return 0;
        }else{
            //查得到就返回ID
            return didaUsers.get(0).getUserId();
        }
    }
}
