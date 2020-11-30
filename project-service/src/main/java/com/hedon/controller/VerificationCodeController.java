package com.hedon.controller;


import com.hedon.service.IVerificationCodeService;
import common.code.ResultCode;
import common.exception.ServiceException;
import common.vo.common.ResponseBean;
import common.vo.request.RegisterRequestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  验证码
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-11-30
 */
@RestController
@RequestMapping("/project/code")
public class VerificationCodeController {

    @Autowired
    IVerificationCodeService verificationCodeService;


    @PostMapping("/register")
    public ResponseBean sendRegisterCode(@RequestBody RegisterRequestVo vo){
        if (vo == null){
            return ResponseBean.fail(ResultCode.GET_VERIFICATION_CODE_FAILED);
        }
        //发送验证码
        try {
            verificationCodeService.sendRegisterCode(vo.getUsername());
        }catch (ServiceException e){
            return e.getFailResponse();
        }
        return ResponseBean.success();
    }


}
