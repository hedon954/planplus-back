package com.hedon.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hedon.dto.DidaUserDTO;
import com.hedon.service.IDidaUserService;
import common.code.ResultCode;
import common.entity.DidaUser;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import common.vo.response.DidaUserResponseVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@RestController
@RequestMapping("/project/user")
public class DidaUserController {

    @Autowired
    IDidaUserService didaUserService;

    /**
     * 接口1.5 获取用户信息
     *
     * @author Hedon
     * @create 2020.10.23
     * @param userId 用户ID
     * @return 包含用户信息的 ResponseBean
     */
    @ApiOperation(value = "接口1.5 获取用户信息",httpMethod = "GET")
    @ApiImplicitParam(name = "userId",value = "用户ID",dataType = "Integer",paramType = "path",required = true)
    @GetMapping("/{userId}")
    public ResponseBean getUserById(@PathVariable("userId")Integer userId){

        //检查id是否为空
        if (userId == null){
            return ResponseBean.fail(ResultCode.EMPTY_USER_ID);
        }
        //查询用户信息
        DidaUserResponseVo didaUserResponseVo;
        try{
            DidaUser didaUser = didaUserService.getUserById(userId);
            didaUserResponseVo = new DidaUserResponseVo(didaUser);
        }catch (ServiceException e){
            //从抛出的异常信息中封装出一个 ResponseBean6
            return e.getFailResponse();
        }
        //封装信息，返回给前端
        return ResponseBean.success(didaUserResponseVo);
    }

    @PutMapping("/{userId}")
    public ResponseBean upadtePassword(@PathVariable("userId")Integer userId,@RequestBody String json)throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        DidaUserDTO didaUserDTO = objectMapper.readValue(json, DidaUserDTO.class);
        //DidaUserDTO didaUserDTO = objectMapper.convertValue(objectMapper.readValue(json, ObjectNode.class).get("userInfo"),DidaUserDTO.class);
        try{
            DidaUser didaUser = new DidaUser();
            BeanUtils.copyProperties(didaUserDTO,didaUser);
            didaUserService.updateUserInfoById(didaUser);
            return ResponseBean.success();
        }catch (Exception e) {
            e.printStackTrace();
            return ResponseBean.fail(ResultCode.ERROR);
        }
    }



}
