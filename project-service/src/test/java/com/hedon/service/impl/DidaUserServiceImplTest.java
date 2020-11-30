package com.hedon.service.impl;


import com.hedon.ProjectApplication;
import common.entity.DidaUser;
import common.mapper.DidaUserMapper;
import common.vo.request.DidaTaskRequestVo;
import common.vo.request.DidaUserRequestVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest(classes = {ProjectApplication.class})
public class DidaUserServiceImplTest {

    @Autowired
    DidaUserServiceImpl didaUserService;

    @Autowired
    DidaUserMapper didaUserMapper;


    @Test
    public void test1(){
        DidaUser userById = didaUserService.getUserById(1);
        System.out.println(userById);
    }

    @Test
    public void test2(){
        DidaUser user = didaUserMapper.getUserByPhoneOrEmail("171725713@qq.com");
        System.out.println(user);
    }
}