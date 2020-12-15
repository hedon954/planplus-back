package com.hedon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.hedon.service.IBaiduInfoService;
import com.hedon.service.INotificationService;
import common.dto.TaskNotificationDto;
import common.entity.BaiduInfo;
import common.entity.DidaTask;
import common.entity.DidaUser;
import common.entity.DidaUserTask;
import common.mapper.BaiduInfoMapper;
import common.mapper.DidaTaskMapper;
import common.mapper.DidaUserMapper;
import common.mapper.DidaUserTaskMapper;
import common.vo.common.BaiduTokenInfo;
import common.vo.common.NotificationResultInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author Hedon Wang
 * @create 2020-11-25 21:00
 */
@Service
@Slf4j
public class NotificationServiceImpl implements INotificationService {

    private static final int START = 1;
    private static final int FINISH = 2;

    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private DidaTaskMapper didaTaskMapper;

    @Autowired
    private DidaUserTaskMapper didaUserTaskMapper;

    @Autowired
    private DidaUserMapper didaUserMapper;

    @Autowired
    private BaiduInfoMapper baiduInfoMapper;

    /**
     * 读取环境变量
     */
    @Autowired
    private Environment environment;

    /**
     * 百度信息 Service
     */
    @Autowired
    private IBaiduInfoService baiduInfoService;


    /**
     * 每一分钟扫描一次：判断是否需要给用户发送开始任务通知
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     */
    @Override
    @Scheduled(cron = "0 * * * * *")
    public void handleTimedTaskStartRemind(){
        log.info("正在检测需要进行通知的即将开始任务......");

        //补充百度信息 access_token 和 token_time
        BaiduInfo planPlusInfo = baiduInfoService.getPlanPlusInfo();
        //检查 token 是否过期
        if (planPlusInfo.tokenIsExpired()){
            //如果过期，则重新获取 token
            try{
                getBaiduToken(planPlusInfo);
            }catch (Exception e){
                log.error("更新百度 token 失败，原因为为：({})",e.getMessage());
                return;
            }
        }
        //获取当前时间
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(dtf);
        nowStr = nowStr.substring(0,16);
        QueryWrapper<DidaTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_status",0)
                .likeRight("task_remind_time",nowStr);
        List<DidaTask> didaTasks = didaTaskMapper.selectList(queryWrapper);
        for (DidaTask didaTask: didaTasks){
            //查询用户信息
            Integer taskId = didaTask.getTaskId();
            System.out.println("taskId = " + taskId);
            QueryWrapper<DidaUserTask> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("dida_task_id",taskId);
            DidaUserTask didaUserTask = didaUserTaskMapper.selectOne(queryWrapper1);
            DidaUser didaUser = didaUserMapper.selectById(didaUserTask.getDidaUserId());
            //封装dto
            TaskNotificationDto dto = new TaskNotificationDto();
            dto.setAccessToken(planPlusInfo.getAccessToken());
            dto.setSceneId(didaTask.getTaskFormId());
            dto.setTouserOpenId(didaUser.getUserOpenId());
            dto.setPage("/pages/modification/modification?taskId="+didaTask.getTaskId());
            sendTimedTaskMsgToUser(dto,didaTask,START);
        }

    }

    /**
     * 每一分钟扫描一次：判断是否需要给用户发送结束任务通知
     *
     * @author Jiahan Wang
     * @create 2020.12.16
     */
    @Override
    @Scheduled(cron = "30 * * * * *")
    public void handleTimedTaskFinishRemind() {
        log.info("正在检测需要进行通知的已达完成时间的任务......");

        //补充百度信息 access_token 和 token_time
        BaiduInfo planPlusInfo = baiduInfoService.getPlanPlusInfo();
        //检查 token 是否过期
        if (planPlusInfo.tokenIsExpired()){
            //如果过期，则重新获取 token
            try{
                getBaiduToken(planPlusInfo);
            }catch (Exception e){
                log.error("更新百度 token 失败，原因为为：({})",e.getMessage());
                return;
            }
        }
        //获取当前时间
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String nowStr = now.format(dtf);
        nowStr = nowStr.substring(0,16);
        QueryWrapper<DidaTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_status",0)
                .likeRight("task_predicted_finish_time",nowStr);
        List<DidaTask> didaTasks = didaTaskMapper.selectList(queryWrapper);
        for (DidaTask didaTask: didaTasks){
            //查询用户信息
            Integer taskId = didaTask.getTaskId();
            System.out.println("taskId = " + taskId);
            QueryWrapper<DidaUserTask> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.eq("dida_task_id",taskId);
            DidaUserTask didaUserTask = didaUserTaskMapper.selectOne(queryWrapper1);
            DidaUser didaUser = didaUserMapper.selectById(didaUserTask.getDidaUserId());
            //封装dto
            TaskNotificationDto dto = new TaskNotificationDto();
            dto.setAccessToken(planPlusInfo.getAccessToken());
            dto.setSceneId(didaTask.getTaskFormId());
            dto.setTouserOpenId(didaUser.getUserOpenId());
            dto.setPage("/pages/modification/modification?taskId="+didaTask.getTaskId());
            sendTimedTaskMsgToUser(dto,didaTask,FINISH);
        }
    }

    /**
     * 发送通知给用户
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param dto
     * @param didaTask
     */
    private void sendTimedTaskMsgToUser(TaskNotificationDto dto, DidaTask didaTask,int time) {
        try{
            //请求链接
            String sendTemplateMessageUrl = "https://openapi.baidu.com/rest/2.0/smartapp/template/send?access_token="+dto.getAccessToken();
            //请求头
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            //请求参数
            MultiValueMap<String,Object> params = new LinkedMultiValueMap<>();
            params.add("template_id",dto.getTemplateId());
            params.add("touser_openId",dto.getTouserOpenId());
            params.add("data",objectToJsonStr(didaTask,time));
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
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param planPlusInfo
     */
    private void getBaiduToken(BaiduInfo planPlusInfo) throws Exception{
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
        planPlusInfo.setAccessToken(tokenInfo.getAccess_token());
        planPlusInfo.setTokenTime(LocalDateTime.now());
        baiduInfoMapper.updateById(planPlusInfo);
    }

    /**
     * 将一个 DidaTask 转为 JSON 字符串，且要符合以下规律：
     * {"keyword1":{"value":"2018-09-06"},"keyword2":{"value":"kfc"},"keyword3":{"value":"kfc"}}
     *
     * @author Jiahan Wang
     * @create 2020.11.26
     * @param didaTask
     * @return
     */
    private String objectToJsonStr(DidaTask didaTask, int time) {
        String jsonStr = "";
        //参数1：日程描述
        jsonStr += "{\"keyword1\":{\"value\": \""+didaTask.getTaskContent()+"\"},";
        //参数2：开始时间
        jsonStr += "\"keyword2\":{\"value\": \""+didaTask.getTaskStartTime()+"\"},";
        //参数3：结束时间
        jsonStr += "\"keyword3\":{\"value\": \""+didaTask.getTaskPredictedFinishTime()+"\"},";
        //参数4：日程地点
        jsonStr += "\"keyword4\":{\"value\": \""+didaTask.getTaskPlace()+"\"},";
        if (time == START){
            //参数5：开始任务的备注
            jsonStr += "\"keyword5\":{\"value\": \""+"任务开始时间要到啦！进入小程序点击\"开始任务\"吧~"+"\"}}";
        }else if (time == FINISH){
            //参数5：结束任务的备注
            jsonStr += "\"keyword5\":{\"value\": \""+"任务设定的完成时间到啦！完成任务的话进入小程序点击\"完成任务\"吧~"+"\"}}";
        }else {
            //参数5：备注
            jsonStr += "\"keyword5\":{\"value\": \""+"备注"+"\"}}";
        }

        return jsonStr;
    }
}
