package com.hedon.service.impl;


import com.hedon.ProjectApplication;
import common.entity.DidaUser;
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


    @Test
    public void test1(){
        DidaUser userById = didaUserService.getUserById(1);
        System.out.println(userById);
    }

    /**
     * 测试updateUserByVo
     *
     * @author yang jie
     * @create 2020.10.24
     */
    @Test
    public void test2() {
        DidaUserRequestVo requestVo = new DidaUserRequestVo();
        requestVo.setUserId(1);
        requestVo.setUserGender(0);
        requestVo.setUserBirthday(LocalDateTime.now());
        requestVo.setUserNickname("hhhh");
        didaUserService.updateUserByVo(requestVo);
    }

}