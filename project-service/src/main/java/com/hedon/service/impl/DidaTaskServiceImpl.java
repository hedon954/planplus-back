package com.hedon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.feign.NotificationFeignService;
import com.hedon.service.IDidaTaskService;
import common.code.ResultCode;
import common.dto.TaskNotificationDto;
import common.entity.DidaTask;
import common.entity.DidaUser;
import common.entity.DidaUserTask;
import common.exception.ServiceException;
import common.mapper.DidaTaskMapper;
import common.mapper.DidaUserMapper;
import common.mapper.DidaUserTaskMapper;
import common.util.timenlp.nlp.TimeNormalizer;
import common.util.timenlp.nlp.TimeUnit;
import common.vo.common.ResponseBean;
import common.vo.request.DidaTaskRequestVo;
import common.vo.request.DidaTaskSentenceRequestVo;
import common.vo.response.DidaTaskResponseVo;
import common.vo.response.DidaTaskStateResponseVo;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.*;
import java.util.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@Service
public class DidaTaskServiceImpl extends ServiceImpl<DidaTaskMapper, DidaTask> implements IDidaTaskService {

    @Autowired
    DidaTaskMapper didaTaskMapper;

    @Autowired
    DidaUserTaskMapper didaUserTaskMapper;

    @Autowired
    NotificationFeignService notificationFeignService;

    @Autowired
    DidaUserMapper didaUserMapper;

    /**
     * 创建新任务
     *
     * @author yang jie
     * @create 2020-10-25 23:50
     * @param userId
     * @param taskInfo
     */
    @Override
    @Transactional
    public Integer createTask(Integer userId, DidaTaskRequestVo taskInfo) {

        //修改任务表
        DidaTask didaTask = DidaTaskRequestVo.toDidaTask(taskInfo);
        //判断任务开始时间是否早于结束时间
        LocalDateTime begin = didaTask.getTaskStartTime();
        LocalDateTime end = didaTask.getTaskPredictedFinishTime();
        if(begin.isAfter(end)) {
            throw new ServiceException(ResultCode.TASK_TIME_INVALID);
        }
        didaTaskMapper.insert(didaTask);

        //获取新建任务的taskId
        Integer taskId = didaTask.getTaskId();

        //发送通知
        ResponseBean responseBean = sendNotification(userId,didaTask,taskInfo.getTaskFormId());
        if (responseBean.getCode() != 1000L){
            //如果发送消息不成功，那就要回滚 => 这里先手动回滚删除前面插入的数据
            didaTaskMapper.deleteById(taskId);
            throw new ServiceException(ResultCode.TIMED_TASK_CREATE_FAILED);
        }

        //修改用户任务表
        DidaUserTask didaUserTask = new DidaUserTask();
        didaUserTask.setDidaTaskId(taskId);
        didaUserTask.setDidaUserId(userId);
        didaUserTaskMapper.insert(didaUserTask);

        return taskId;
    }

    /**
     * 按日期查询待办任务 多表查询
     *
     * @author yang jie
     * @create 2020-10-26 22:45
     * @param userId
     * @param date
     * @return
     */
    @Override
    public ArrayList<DidaTaskResponseVo> getTasksByDate(Integer userId, LocalDate date) {
        ArrayList<DidaTask> didaTasks = didaTaskMapper.selectByDate(userId, date.toString() + "%");
        return tasksToVos(didaTasks);
    }


    /**
     * 开始任务，修改任务状态
     *
     * @author yang jie
     * @create 2020-10-29 11:30
     * @param taskId
     * @param userId
     */
    @Override
    public void startTask(Integer taskId, Integer userId) {

        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        DidaTask task = didaTaskMapper.selectById(taskId);

        //判断任务是否存在
        if(task == null) {
            throw new ServiceException(ResultCode.TASK_NOT_EXIST);
        }

        task.setTaskStatus(1);
        didaTaskMapper.updateById(task);
    }


    /**
     * 推迟任务，修改任务开始时间、预计结束时间
     *
     * @author yang jie
     * @create 2020-10-29 16:50
     * @param taskId
     * @param userId
     * @param delayTime
     */
    @Override
    public void delayTask(Integer taskId, Integer userId, Integer delayTime, String formId) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        //获取任务的原开始时间、原预计结束时间
        DidaTask task = didaTaskMapper.selectById(taskId);

        //判断任务是否存在
        if(task == null) {
            throw new ServiceException(ResultCode.TASK_NOT_EXIST);
        }

        LocalDateTime startTime = task.getTaskStartTime();
        LocalDateTime predictedFinishTime = task.getTaskPredictedFinishTime();

        //修改任务开始时间、原预计结束时间
        startTime = startTime.plusMinutes(delayTime);
        predictedFinishTime = predictedFinishTime.plusMinutes(delayTime);

        //更新数据库
        task.setTaskStartTime(startTime);
        task.setTaskPredictedFinishTime(predictedFinishTime);
        task.setTaskRemindTime(startTime.minusMinutes(task.getTaskAdvanceRemindTime()));
        task.setTaskFormId(formId);

        /*
                        暂时不需要
        //发送通知
        ResponseBean responseBean = sendNotification(userId, task, formId);
        if (responseBean.getCode() != 1000L){
            //如果发送消息不成功，抛出异常
            throw new ServiceException(ResultCode.TASK_DELAY_FAILED);
        }
         */

        didaTaskMapper.updateById(task);
    }






    /**
     * 结束任务，改任务状态，写任务实际结束时间、花费时间
     *
     * @author yang jie
     * @create 2020-10-29 15:35
     * @param taskId
     * @param userId
     * @param formId
     */
    @Override
    public void finishTask(Integer taskId, Integer userId, String formId) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        //获取任务开始时间和当前时间
        DidaTask didaTask = didaTaskMapper.selectById(taskId);

        //判断任务是否存在
        if(didaTask == null) {
            throw new ServiceException(ResultCode.TASK_NOT_EXIST);
        }


        LocalDateTime startTime = didaTask.getTaskStartTime();
        LocalDateTime finishTime = LocalDateTime.now();

        //计算花费时间
        Duration duration = Duration.between(startTime, finishTime);

        int day = (int) duration.toDays();
        int hour = (int) duration.toHours() % (day == 0? 1: 24);
        int minutes = (int) duration.toMinutes() % (hour == 0? 1: 60);
        String consumedTime = (day==0? "": day + "D ") + (hour==0? "": hour + "h ") + minutes + "m";

        //更新数据库
        didaTask.setTaskRealFinishTime(finishTime);
        didaTask.setTaskConsumedTime(consumedTime);
        didaTask.setTaskStatus(2);
        didaTaskMapper.updateById(didaTask);

        /**
         * 判断任务频率
         * 若任务仅执行一次，则写任务结束时间；
         * 若任务执行多次，则按频率推迟任务开始时间和提前提醒时间
         */
        generateTask(didaTask, userId, formId);

    }

    /**
     * 迭代任务
     *
     * @param didaTask
     * @param userId
     */
    @Transactional
    public void generateTask(DidaTask didaTask, Integer userId, String formId) {

        //修改任务表
        didaTask.setTaskId(null);
        didaTask.setTaskFormId(formId);
        didaTask.setTaskRealFinishTime(null);
        didaTask.setTaskConsumedTime(null);
        didaTask.setTaskStatus(0);

        //判断频率
        switch (didaTask.getTaskRate()){
            //每天
            case 1:
                didaTask.setTaskStartTime(didaTask.getTaskStartTime().plusDays(1));
                didaTask.setTaskPredictedFinishTime(didaTask.getTaskPredictedFinishTime().plusDays(1));
                didaTask.setTaskRemindTime(didaTask.getTaskStartTime().minusMinutes(didaTask.getTaskAdvanceRemindTime()));
                break;
            //每周
            case 2:
                didaTask.setTaskStartTime(didaTask.getTaskStartTime().plusWeeks(1));
                didaTask.setTaskPredictedFinishTime(didaTask.getTaskPredictedFinishTime().plusWeeks(1));
                didaTask.setTaskRemindTime(didaTask.getTaskStartTime().minusMinutes(didaTask.getTaskAdvanceRemindTime()));
                break;
            //每月
            case 3:
                didaTask.setTaskStartTime(didaTask.getTaskStartTime().plusMonths(1));
                didaTask.setTaskPredictedFinishTime(didaTask.getTaskPredictedFinishTime().plusMonths(1));
                didaTask.setTaskRemindTime(didaTask.getTaskStartTime().minusMinutes(didaTask.getTaskAdvanceRemindTime()));
                break;
            default:
                return;
        }

        didaTaskMapper.insert(didaTask);

        //获取新建任务的taskId
        Integer taskId = didaTask.getTaskId();

        //修改用户任务表
        DidaUserTask didaUserTask = new DidaUserTask();
        didaUserTask.setDidaTaskId(taskId);
        didaUserTask.setDidaUserId(userId);
        didaUserTaskMapper.insert(didaUserTask);

    }


    /**
     * 修改任务内容
     *
     * @author yang jie
     * @create 2020-10-29 19:35
     * @param taskId
     * @param userId
     * @param taskInfo
     */
    @Override
    public void modifyTask(Integer taskId, Integer userId, DidaTaskRequestVo taskInfo) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        DidaTask task = didaTaskMapper.selectById(taskId);

        //判断任务是否存在
        if(task == null) {
            throw new ServiceException(ResultCode.TASK_NOT_EXIST);
        }

        //判断任务开始时间是否早于结束时间
        if(taskInfo.getTaskStartTime().isAfter(taskInfo.getTaskPredictedFinishTime())) {
            throw new ServiceException(ResultCode.TASK_TIME_INVALID);
        }

        /*
        //检查是否修改了任务的开始时间和提前提醒时间
        Long oldTaskStartTime = task.getTaskStartTime().toEpochSecond(ZoneOffset.UTC);
        Long newTaskStartTime = taskInfo.getTaskStartTime().toEpochSecond(ZoneOffset.UTC);
        Integer oldTaskAdvanceRemindTime = task.getTaskAdvanceRemindTime();
        Integer newTaskAdvanceRemindTime = taskInfo.getTaskAdvanceRemindTime();
        Boolean needNotify = false;
        if (!oldTaskStartTime.equals(newTaskStartTime) ||
            !oldTaskAdvanceRemindTime.equals(newTaskAdvanceRemindTime)){
            needNotify = true;
        }
         */

        task.setTaskContent(taskInfo.getTaskContent());
        task.setTaskPlace(taskInfo.getTaskPlace());
        task.setTaskRate(taskInfo.getTaskRate());
        task.setTaskStartTime(taskInfo.getTaskStartTime());
        task.setTaskPredictedFinishTime(taskInfo.getTaskPredictedFinishTime());
        task.setTaskAdvanceRemindTime(taskInfo.getTaskAdvanceRemindTime());
        task.setTaskRemindTime(taskInfo.getTaskStartTime().minusMinutes(taskInfo.getTaskAdvanceRemindTime()));

        /*
        //发送通知
        if (needNotify){
            ResponseBean responseBean = sendNotification(userId, task, taskInfo.getTaskFormId());
            if (responseBean.getCode() != 1000L){
                //如果发送消息不成功，抛出异常
                throw new ServiceException(ResultCode.TASK_DELAY_FAILED);
            }
        }
         */


        //更新数据库
        didaTaskMapper.updateById(task);
    }


    /**
     * 按状态查询任务 多表查询
     *
     * @author yang jie
     * @create 2020-10-29 20:20
     * @param userId
     * @param taskStatus
     */
    @Override
    public ArrayList<DidaTaskResponseVo> getTasksByStatus(Integer userId, Integer taskStatus) {
        ArrayList<DidaTask> didaTasks = didaTaskMapper.selectByStatus(userId, taskStatus);
        return tasksToVos(didaTasks);
    }


    /**
     * 查询所有任务
     *
     * @author yang jie
     * @create 2020-10-29 20:50
     * @param userId
     */
    @Override
    public ArrayList<DidaTaskResponseVo> getAllTasks(Integer userId) {
        ArrayList<DidaTask> didaTasks = didaTaskMapper.selectAll(userId);
        return tasksToVos(didaTasks);
    }


    /**
     * 删除任务
     *
     * @author yang jie
     * @create 2020-10-29 21:10
     * @param taskId
     * @param userId
     */
    @Override
    @Transactional()
    public void deleteTask(Integer taskId, Integer userId) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        //修改任务表
        didaTaskMapper.deleteById(taskId);

        //修改用户任务表
        QueryWrapper<DidaUserTask> queryWrapper = new QueryWrapper();
        queryWrapper.eq("dida_task_id", taskId);
        didaUserTaskMapper.delete(queryWrapper);
    }

    /**
     * 查询单个任务
     *
     * @author yang jie
     * @create 2020-11-05 11:50
     * @param taskId
     * @param userId
     */
    @Override
    public DidaTaskResponseVo getTaskById(Integer taskId, Integer userId) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        DidaTaskResponseVo didaTaskResponseVo = new DidaTaskResponseVo(didaTaskMapper.selectById(taskId));

        return didaTaskResponseVo;
    }


    /**
     * 将任务保存至草稿箱
     *
     * @author yang jie
     * @create 2020-11-06 23:50
     * @param taskId
     * @param userId
     */
    @Override
    public void draftTask(Integer taskId, Integer userId) {
        /**
         * 判断任务和用户是否匹配，
         * 若不匹配则抛出异常
         */
        judgeUserTaskMatch(taskId, userId);

        DidaTask task = didaTaskMapper.selectById(taskId);

        //判断任务是否存在
        if(task == null) {
            throw new ServiceException(ResultCode.TASK_NOT_EXIST);
        }

        task.setTaskStatus(3);
        didaTaskMapper.updateById(task);
    }


    /**
     * 判断用户和人物之间是否存在对应关系
     *
     * @author yang jie
     * @create 2020-10-29 16:30
     * @param taskId
     * @param userId
     * @return
     */
    private void judgeUserTaskMatch(Integer taskId, Integer userId) {
        QueryWrapper<DidaUserTask> queryWrapper = new QueryWrapper<>();
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("dida_user_id", userId);
        map.put("dida_task_id", taskId);
        queryWrapper.allEq(map);

        /**
         * 如果查询结果为空，
         * 说明用户任务表中不存在该条数据，即任务和用户不匹配，或者任务不存在
         * 此时抛出异常
         */
        if(didaUserTaskMapper.selectList(queryWrapper) == null || didaUserTaskMapper.selectList(queryWrapper).size() == 0) {
            throw new ServiceException(ResultCode.USER_TASK_MISMATCHING);
        }
    }


    /**
     * 抽取出的公共方法，将查询到的任务列表转换成responseVo列表
     *
     * @author yang jie
     * @create 2020-10-29 20:35
     * @param didaTasks
     * @return
     */
    private ArrayList<DidaTaskResponseVo> tasksToVos(ArrayList<DidaTask> didaTasks) {
        ArrayList<DidaTaskResponseVo> didaTaskResponseVos = new ArrayList<>();
        for (DidaTask didaTask : didaTasks) {
            //将任务信息中需要的字段重新封装
            didaTaskResponseVos.add(new DidaTaskResponseVo(didaTask));
        }
        return didaTaskResponseVos;
    }

    /**
     * 调用通知模块发送通知
     *
     * @author Jiahan Wang
     * @create 2020.11.11
     * @param userId    用户ID
     * @param didaTask  任务体
     * @param formId    表单ID
     * @return
     */
    public ResponseBean sendNotification(Integer userId, DidaTask didaTask,String formId){
        DidaUser didaUser = didaUserMapper.selectById(userId);
        TaskNotificationDto dto = new TaskNotificationDto();
        //设置任务ID
        dto.setTaskId(didaTask.getTaskId());
        //设置延迟时间
        long nowEpochSecond = LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond();
        long startEpochSecond = didaTask.getTaskStartTime().toInstant(ZoneOffset.UTC).getEpochSecond();
        Long expiration = startEpochSecond - nowEpochSecond;
        Integer taskAdvanceRemindTime = didaTask.getTaskAdvanceRemindTime();
        Long advance = 60L * taskAdvanceRemindTime;
        //检查设置的提前提醒时间是否合理
        if (expiration > (advance + 10L)){
            expiration -= (advance + 10);
        }else if (expiration >= 0){
            //比如设置15分钟，但是现在离开始任务只有10分钟，那就立即通知
            expiration = 1L;
        }else{
            //如果当前时间早于任务开始时间，那么发送通知失败
            expiration = -1L;
        }
        dto.setExpiration(expiration);
        dto.setSceneId(formId);
        dto.setTouserOpenId(didaUser.getUserOpenId());
        dto.setPage("/pages/modification/modification?taskId="+didaTask.getTaskId());

        System.out.println("project-service dto : " + dto);
        return notificationFeignService.sendNotificationMsg(dto);
    }


    /**
     * 根据一句话创建任务
     *
     * @author Jiahan Wang
     * @create 2020.11.24
     * @param userId    用户ID
     * @param taskInfo  任务信息
     * @return
     */
    @Override
    @Transactional
    public DidaTask createTaskBySentence(Integer userId, DidaTaskSentenceRequestVo taskInfo) throws URISyntaxException,ServiceException{

        //创建任务体
        DidaTask didaTask = new DidaTask();

        /**
         * 明天下午三点到五点在计算机学院202教室考计组
         */

        //①抽取出任务时间
        Map<String, Object> times = extractTime(taskInfo.getTaskInfo());
        didaTask.setTaskStartTime((LocalDateTime) times.get("startTime"));
        didaTask.setTaskPredictedFinishTime((LocalDateTime) times.get("finishTime"));
        didaTask.setTaskRemindTime(didaTask.getTaskStartTime().plusMinutes(-didaTask.getTaskAdvanceRemindTime()));
        String timeStr = (String) times.get("timeStr");

        //②抽取出任务地点和内容
        Map<String, String> addressAndContent = extractAddressAndContent(timeStr, taskInfo.getTaskInfo());
        didaTask.setTaskContent(addressAndContent.get("content"));
        didaTask.setTaskPlace(addressAndContent.get("address"));

        //保存 formId
        didaTask.setTaskFormId(taskInfo.getTaskFormId());

        //插入任务
        didaTaskMapper.insert(didaTask);

        //获取新建任务的taskId
        Integer taskId = didaTask.getTaskId();

        /*
                            RabbitMQ 队列问题后面再改进
        //发送通知
        ResponseBean responseBean = sendNotification(userId,didaTask,taskInfo.getTaskFormId());
        if (responseBean.getCode() != 1000L){
            //如果发送消息不成功，那就要回滚 => 这里先手动回滚删除前面插入的数据
            didaTaskMapper.deleteById(taskId);
            throw new ServiceException(ResultCode.TIMED_TASK_CREATE_FAILED);
        }
         */

        //修改用户任务表
        DidaUserTask didaUserTask = new DidaUserTask();
        didaUserTask.setDidaTaskId(taskId);
        didaUserTask.setDidaUserId(userId);
        didaUserTaskMapper.insert(didaUserTask);

        return didaTask;
    }

    /**
     * 获取近一周的任务状态
     *
     * @param userId 用户ID
     * @return
     * @author Ruolin
     * @create 2020.12.05
     */
    @Override
    public DidaTaskStateResponseVo getTaskStateForThisWeek(Integer userId, LocalDate date) {
        //任务状态，包括任务总数和已完成任务总数
        int[][] taskState = new int[2][7];
        //任务完成百分比
        float[] completePercentage = new float[7];
        //近一周日期
        String[] dateOfWeek = new String[7];

        for(int i=0;i<7;i++){
            date = date.plusDays(-1);
            //获取月份和日期部分
            dateOfWeek[i] = date.toString().substring(6,11);
            //获取这一天的任务
            ArrayList<DidaTask> didaTasks = didaTaskMapper.selectByDate(userId, date.toString() + "%");
            taskState[0][i]=didaTasks.size();
            for (DidaTask task:didaTasks) {
                if(task.getTaskStatus()==2) {
                    taskState[1][i]++;
                }
            }
            //计算完成率
            completePercentage[i] = taskState[1][i]/taskState[0][i]*100;
        }
        //构建返回体
        DidaTaskStateResponseVo didaTaskStateResponseVo = new DidaTaskStateResponseVo();
        didaTaskStateResponseVo.setNumOfTasks(taskState[0]);
        didaTaskStateResponseVo.setNumOfFinishedTasks(taskState[1]);
        didaTaskStateResponseVo.setCompletePercentage(completePercentage);
        return didaTaskStateResponseVo;
    }

    /**
     * 从句子中抽取出时间成分
     *
     * @author Jiahan Wang
     * @create 2020.11.24
     * @param sentence 句子
     * @return 开始时间、结束时间、抽取出的时间语句
     */
    public Map<String,Object> extractTime(String sentence) throws URISyntaxException {

        Map<String,Object> map = new HashMap<>();

        URL url = TimeNormalizer.class.getResource("/TimeExp.m");
        TimeNormalizer normalizer = new TimeNormalizer(url.toURI().toString());
        normalizer.setPreferFuture(true);

        //抽取时间
        normalizer.parse(sentence);
        TimeUnit[] unit = normalizer.getTimeUnit();
        for (TimeUnit timeUnit:unit){
            System.out.println(timeUnit);
        }
        System.out.println(sentence);
        
        //先判断时间个数
        //如果没抽取到时间，则抛出异常
        if (unit.length < 1){
            throw new ServiceException("创建任务失败，请在任务信息中说明任务开始时间！",ResultCode.TIMED_TASK_CREATE_FAILED);
        }
        //如果是有一个时间，那么就是瞬时任务，开始时间和结束时间相等
        if (unit.length == 1){
            Date date = unit[0].getTime();
            LocalDateTime startTime = parseDateToLocalDateTime(date);

            //TODO:漏洞：早上、中午、下午、晚上都会识别为第二天，这个时间识别器的bug，后面解决
            String timeStr = unit[0].Time_Expression;
            if (!timeStr.contains("今") &&
                            (timeStr.startsWith("早上") ||
                            timeStr.startsWith("上午") ||
                            timeStr.startsWith("中午") ||
                            timeStr.startsWith("下午") ||
                            timeStr.startsWith("晚上")) &&
               timeStr.contains("点")){
                //TODO:这里先往前推一天
                startTime = startTime.plusDays(-1);
            }

            //如果只有日期，没有时间，那么默认就是早上9点，如（"明天去青岛"），那么就是明天早上9点去青岛
            if (unit[0].getIsAllDayTime() == true){
                startTime = startTime.plusHours(9);
            }
            //开始时间不能在当前时间之前
            if (startTime.isBefore(LocalDateTime.now())){
                throw new ServiceException("任务开始时间不能早于当前时间",ResultCode.TASK_TIME_INVALID);
            }
            map.put("startTime",startTime);
            map.put("finishTime",startTime);
            map.put("timeStr",timeStr);
        }
        //如果有两个时间，那么第一个就是开始时间，第二个就是结束时间
        if (unit.length >=2){
            Date firstTime = unit[0].getTime();
            Date secondTime = unit[1].getTime();
            //早的那个是开始时间
            LocalDateTime startTime = parseDateToLocalDateTime(firstTime.before(secondTime) ? firstTime : secondTime);
            LocalDateTime finishTime = parseDateToLocalDateTime(secondTime.after(firstTime) ? secondTime : firstTime);

            //TODO:漏洞：早上、中午、下午、晚上都会识别为第二天，这个时间识别器的bug，后面解决
            String timeStr = unit[0].Time_Expression + "-" + unit[1].Time_Expression;
            if (!timeStr.contains("今") &&
                    (timeStr.startsWith("早上") ||
                            timeStr.startsWith("上午") ||
                            timeStr.startsWith("中午") ||
                            timeStr.startsWith("下午") ||
                            timeStr.startsWith("晚上")) &&
                    timeStr.contains("点")){
                //TODO:这里先往前推一天
                startTime = startTime.plusDays(-1);
                finishTime = finishTime.plusDays(-1);
            }
            //判断开始时间是否早于当前时间
            if (startTime.isBefore(LocalDateTime.now())){
                throw new ServiceException("任务开始时间不能早于当前时间",ResultCode.TASK_TIME_INVALID);
            }
            map.put("startTime",startTime);
            map.put("finishTime",finishTime);
            map.put("timeStr",timeStr);
        }
        return map;
    }

    /**
     * 将 Date 转换为 LocalDatetime
     *
     * @author Jiahan Wang
     * @create 2020.11.24
     * @param date
     * @return
     */
    public LocalDateTime parseDateToLocalDateTime(Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant,zoneId);
    }

    /**
     * 从句子中抽取出地址成分和内容成分
     *
     * @param timeStr  时间成分
     * @param sentence 任务信息
     * @return 地址成分和内容成分
     */
    public Map<String,String> extractAddressAndContent(String timeStr, String sentence){
        Map<String,String> map = new HashMap<>();

        int addressStart = -1;
        int contentStart = -1;

        System.out.println("任务完整句子：" + sentence);

        //去掉时间成分
        String s1 = sentence.substring(timeStr.length());
        System.out.println("去掉时间成分后：" + s1);

        //分词
        Result parse = ToAnalysis.parse(s1);
        System.out.println("分词结果： " + parse);

        List<Term> terms = parse.getTerms();

        //索引值，用来粗略估计哪个动词是地址的，哪个动词是任务内容的
        int index = 0;

        //遍历结果 —— 先找地址起始点
        for (Term term: terms){
            String s = term.toString();
            String[] split = s.split("/");
            //先找找看有没有"在"
            if (StringUtils.equals(split[0],"在")){
                addressStart = term.getOffe();
                index ++;
                break;
            }
            //没有"在"就找到第一个动词
            if (StringUtils.equals(split[1],"v")){
                addressStart = term.getOffe();
                break;
            }
        }

        //识别不到任务地点的话就时间后面全部作为任务内容，没有任务地点
        if (addressStart < 0){
            map.put("content",s1);
            map.put("address","");
            return map;
        }

        //遍历结果 —— 往后找到任务内容的起始位置
        for (Term term: terms){
            String s = term.toString();
            String[] split = s.split("/");
            //找到第2个动词，或者是"在"后面的动词
            if (StringUtils.equals(split[1],"v")){
                //不要第一个动词 —— 那是地址的
                if (index!=0){
                    contentStart = term.getOffe();
                    break;
                }
                index++;
            }
        }
        //动词找不到的话尝试找动名词 vn
        if (contentStart < 0){
            for (Term term: terms){
                String s = term.toString();
                String[] split = s.split("/");
                //找到第一个动名词
                if (StringUtils.equals(split[1],"vn")){
                    contentStart = term.getOffe();
                    break;
                }
            }
        }

        //动名词 vn 还找不到的话就找方位词 f，这里只承认"上"和"下"这两个方位词可能作为动词使用
        if (contentStart < 0){
            for (Term term: terms){
                String s = term.toString();
                String[] split = s.split("/");
                //方位词找最后一个
                if (StringUtils.equals(split[0],"上")||StringUtils.equals(split[0],"下")){
                    contentStart = term.getOffe();
                }
            }
        }

        //如果还找不到，则抛出异常
        if (contentStart < 0){
            throw new ServiceException("识别不到任务内容",ResultCode.TIMED_TASK_CREATE_FAILED);
        }
        //第二个动词 v/vn - end 这个区间就是任务内容
        String content = s1.substring(contentStart);
        map.put("content",content);
        //第一个动词v(或"在") - 第二个动词v/vn 这个区间就是地址内容
        String address = s1.substring(addressStart+1,contentStart);
        map.put("address",address);
        return map;
    }


}
