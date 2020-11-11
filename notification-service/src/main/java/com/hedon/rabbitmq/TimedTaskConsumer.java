package com.hedon.rabbitmq;

import com.hedon.service.IBaiduInfoService;
import common.dto.TaskNotificationDto;
import common.entity.BaiduInfo;
import common.entity.DidaTask;
import common.mapper.BaiduInfoMapper;
import common.mapper.DidaTaskMapper;
import common.vo.common.BaiduTokenInfo;
import common.vo.common.NotificationResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 定时任务 —— 消费者
 *
 * @author Hedon Wang
 * @create 2020-11-06 10:48
 */
@Component
@Slf4j
public class TimedTaskConsumer {

    /**
     * 发送请求的工具
     */
    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 任务 Mapper
     */
    @Autowired
    private DidaTaskMapper didaTaskMapper;

    /**
     * 百度信息 Mapper
     */
    @Autowired
    private BaiduInfoMapper baiduInfoMapper;

    /**
     * 百度信息 Service
     */
    @Autowired
    private IBaiduInfoService baiduInfoService;

    /**
     * 读取环境变量
     */
    @Autowired
    private Environment environment;

    /**
     * 消费消息
     *
     * @param dto 消息
     */
    @RabbitListener(queues = "${mq.consumer.real.queue.name}",containerFactory = "multiListenerContainerFactory")
    public void consumeTimedTaskMsg(@Payload TaskNotificationDto dto){
        if (dto != null){
            try {
                Integer taskId = dto.getTaskId();
                DidaTask didaTask = didaTaskMapper.selectById(taskId);
                //先检查是否存在该任务
                if ( didaTask != null){
                    //再检查任务的状态是否为未进行，未进行的才进行通知
                    if(didaTask.getTaskStatus() == 0){
                        //再检查开始时间与当前开始时间是否相差1分钟内，如果不是，表示已经被推迟了
                        LocalDateTime now = LocalDateTime.now();
                        Instant instantNow = now.toInstant(ZoneOffset.UTC);
                        long epochSecondNow = instantNow.getEpochSecond();
                        LocalDateTime taskStartTime = didaTask.getTaskStartTime();
                        Instant instantStart = taskStartTime.toInstant(ZoneOffset.UTC);
                        long epochSecondStart = instantStart.getEpochSecond();
                        if (Math.abs(epochSecondNow-epochSecondStart) < 60){
                            //相差1分钟内，说明没有被推迟，需要向用户发送通知
                            sendTimedTaskMsgToUser(dto,didaTask);
                        }else{
                            //相差超过1分钟，说明已经被推迟了，不做任何处理
                        }
                    }
                }
            }catch (Exception e){
                log.error("发送通知过程中出现错误，错误信息为：({})",e.getMessage());
            }
        }
    }

    /**
     * 发送通知给用户
     * @param dto
     * @param didaTask
     */
    private void sendTimedTaskMsgToUser(TaskNotificationDto dto, DidaTask didaTask) {
        try{
            //补充百度信息 access_token 和 token_time
            BaiduInfo planPlusInfo = baiduInfoService.getPlanPlusInfo();
            dto.setAccessToken(planPlusInfo.getAccessToken());
            dto.setTokenTime(planPlusInfo.getTokenTime().toEpochSecond(ZoneOffset.UTC));
            //检查 token 是否过期
            if (dto.tokenIsExpired()){
                //如果过期，则重新获取 token
                try{
                    getBaiduToken(dto);
                }catch (Exception e){
                    log.error("更新百度 token 失败，原因为为：({})",e.getMessage());
                    return;
                }
            }
            //请求链接
            String sendTemplateMessageUrl = "https://openapi.baidu.com/rest/2.0/smartapp/template/send?access_token="+dto.getAccessToken();
            //请求头
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            //请求参数
            MultiValueMap<String,Object> params = new LinkedMultiValueMap<>();
            params.add("template_id",dto.getTemplateId());
            params.add("touser_openId",dto.getTouserOpenId());
            params.add("data",objectToJsonStr(didaTask));
            params.add("scene_id",dto.getSceneId());
            params.add("scene_type",dto.getSceneType());
            params.add("page",dto.getPage());
            //请求头
            HttpEntity<MultiValueMap<String,Object>> entity = new HttpEntity<>(params,httpHeaders);
            //发送请求
            ResponseEntity<NotificationResultInfo> exchange = restTemplate.exchange(sendTemplateMessageUrl, HttpMethod.POST, entity, NotificationResultInfo.class);
            //判断是否发送成功
            NotificationResultInfo info = exchange.getBody();
            if (info.getErrno() != 0){
                //不等于0说明发送失败
                log.error("发送通知失败，通知内容为：({})",dto);
                return;
            }
            log.info("发送通知成功，通知内容为：({})",dto);
        }catch (Exception e){
            log.error("发送通知失败，通知内容为：({})",dto,e.fillInStackTrace());
        }

    }

    /**
     * 获取百度 token
     * @param dto
     */
    private void getBaiduToken(TaskNotificationDto dto) throws Exception{
        //请求链接
        String getBaiduTokenUrl = "https://openapi.baidu.com/oauth/2.0/token";
        //请求头
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //请求参数
        MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","client_credentials");
        params.add("client_id",environment.getProperty("baidu.planplus.client-id"));
        params.add("client_secret",environment.getProperty("baidu.planplus.sk"));
        params.add("scope","smartapp_snsapi_base");
        //请求体
        HttpEntity<MultiValueMap<String,String>> entity = new HttpEntity<>(params,httpHeaders);
        //发送请求
        ResponseEntity<BaiduTokenInfo> response = restTemplate.exchange(getBaiduTokenUrl, HttpMethod.POST, entity, BaiduTokenInfo.class);
        //更新 token
        BaiduTokenInfo tokenInfo = response.getBody();
        dto.setAccessToken(tokenInfo.getAccess_token());
        dto.setTokenTime(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        BaiduInfo planPlusInfo = baiduInfoService.getPlanPlusInfo();
        planPlusInfo.setAccessToken(tokenInfo.getAccess_token());
        planPlusInfo.setTokenTime(LocalDateTime.now());
        baiduInfoMapper.updateById(planPlusInfo);
    }

    /**
     * 将一个 DidaTask 转为 JSON 字符串，且要符合以下规律：
     * {"keyword1":{"value":"2018-09-06"},"keyword2":{"value":"kfc"},"keyword3":{"value":"kfc"}}
     *
     *
     * @param didaTask
     * @return
     */
    private String objectToJsonStr(DidaTask didaTask) {
        String jsonStr = "";
        //参数1：日程描述
        jsonStr += "{\"keyword1\":{\"value\": \""+didaTask.getTaskContent()+"\"},";
        //参数2：开始时间
        jsonStr += "\"keyword2\":{\"value\": \""+didaTask.getTaskStartTime()+"\"},";
        //参数3：结束时间
        jsonStr += "\"keyword3\":{\"value\": \""+didaTask.getTaskPredictedFinishTime()+"\"},";
        //参数4：日程地点
        jsonStr += "\"keyword4\":{\"value\": \""+didaTask.getTaskPlace()+"\"},";
        //参数5：备注
        jsonStr += "\"keyword5\":{\"value\": \""+"备注"+"\"}}";
        return jsonStr;
    }
}
