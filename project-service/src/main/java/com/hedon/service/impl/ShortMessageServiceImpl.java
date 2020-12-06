package com.hedon.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import com.hedon.service.IShortMessageService;
import com.zhenzi.sms.ZhenziSmsClient;
import common.code.ResultCode;
import common.vo.common.ResponseBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 短信服务实现类
 *
 * @author Hedon Wang
 * @create 2020-11-29 15:13
 */
@Service
@Slf4j
public class ShortMessageServiceImpl implements IShortMessageService {

    private final String API_URL="https://sms_developer.zhenzikj.com";
    private final String APP_ID = "107313";
    private final String APP_SECRET = "8a910f2f-8561-4c31-bc02-93c019d3c610";
    private final String TEMPLATE_ID = "2511";

    /**
     * 发送验证码
     * @param phoneNumber 电话号码
     */
    @Override
    public ResponseBean sendCode(String phoneNumber) {

        ZhenziSmsClient client = new ZhenziSmsClient(API_URL,APP_ID,APP_SECRET);
        //随机生成验证码
        String code = String.valueOf(new Random(899999).nextInt() + 100000);
        //参数封装
        Map<String,Object> params = new HashMap<>();
        params.put("number",phoneNumber);
        params.put("templateId",TEMPLATE_ID);
        //模板参数
        String[] templateParams = new String[2];
        templateParams[0] = code;
        templateParams[1] = "5分钟";
        params.put("templateParams",templateParams);

        JSONObject json;
        try {
            //发送验证码
            String send = client.send(params);
            json = new JSONObject(send);
            if (json.getInt("code") == 0){
                return ResponseBean.fail(ResultCode.SEND_SMS_FAILED);
            }
            return ResponseBean.success(json);
        }catch (Exception e){
            e.printStackTrace();
            return ResponseBean.fail(ResultCode.SEND_SMS_FAILED);
        }

    }
}
