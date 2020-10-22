package com.hedon.controller;


import com.hedon.service.IUserService;
import common.entity.User;
import common.vo.common.ResponseBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-16
 */
@RestController
@RequestMapping("/oauth/user")
public class UserController {

    @Autowired
    private IUserService userService;

    @GetMapping("/users")
    public ResponseBean getUsers(){
        List<User> users = userService.getUsers();
        return ResponseBean.success(users);
    }

}
