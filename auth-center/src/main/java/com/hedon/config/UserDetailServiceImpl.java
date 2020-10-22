package com.hedon.config;

import common.entity.User;
import common.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * 用户信息类
 *
 * @author Hedon Wang
 * @create 2020-10-16 17:03
 */
@Component
public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    private UserMapper userMapper;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null){
            throw new UsernameNotFoundException("用户名不能为空！");
        }
        //获取用户信息
        User user = userMapper.getUserByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("用户名不存在");
        }
        //返回用户信息
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_ADMIN")   //这里用户的权限需要根据项目具体需求来定
                .build();
    }


}
