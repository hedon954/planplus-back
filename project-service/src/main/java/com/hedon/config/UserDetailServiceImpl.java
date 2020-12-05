package com.hedon.config;

import com.hedon.sercurity.UserDetailsEnhance;
import com.hedon.service.IDidaUserService;
import common.entity.DidaUser;
import common.mapper.DidaUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * 用户信息类
 *
 * @author Hedon Wang
 * @create 2020-10-16 17:03
 */
@Component
public class UserDetailServiceImpl implements UserDetailsService {


    @Autowired
    private DidaUserMapper didaUserMapper;

    @Autowired
    private IDidaUserService didaUserService;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null){
            throw new UsernameNotFoundException("用户名不能为空！");
        }
        //获取用户信息
        DidaUser user = null;
        if (username.startsWith("union_id_")){
            //如果是用 unionId 进行登录的话
            user = didaUserService.getUserByUnionIdWhenLogin(username);
            if (user == null){
                throw new UsernameNotFoundException("用户名不存在");
            }
        }else{
            //手机或邮箱登录
            user = didaUserMapper.getUserByPhoneOrEmail(username);
            if (user == null){
                throw new UsernameNotFoundException("用户名不存在");
            }
        }
        //返回用户信息
        String[] auths = new String[]{"ROLE_ADMIN"};
        UserDetailsEnhance userDetailsEnhance = new UserDetailsEnhance(
                username,
                user.getUserPassword(),
                (Collection) AuthorityUtils.createAuthorityList(auths));
        userDetailsEnhance.setUserId(user.getUserId());
        return userDetailsEnhance;
    }


}
