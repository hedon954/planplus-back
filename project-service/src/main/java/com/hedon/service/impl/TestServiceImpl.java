package com.hedon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.service.ITestService;
import com.netflix.hystrix.contrib.javanica.annotation.DefaultProperties;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import common.code.ResultCode;
import common.entity.Test;
import common.entity.User;
import common.exception.ServiceException;
import common.mapper.TestMapper;
import common.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-16
 */
@Service
@DefaultProperties(defaultFallback = "globalFallbackMethod")  //指定全局 fallback 方法
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements ITestService {


    @Autowired
    private TestMapper testMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Test getById(Integer id){
        return testMapper.selectById(id);
    }

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return
     */
    @HystrixCommand  //允许调用全局 fallback 进行服务降级、熔断
    @Override
    public User getUserById(Integer id) {
        /*
           测试 Hystrix => 测试通过
           int i = 1/0;
         */
        User user =  userMapper.selectById(id);
        if (user == null){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        return user;
    }

    /**
     * 修改用户信息
     *
     * @param user
     * @return
     */
    @Override
    @HystrixCommand(fallbackMethod = "updateUserFallback",commandProperties = {
            @HystrixProperty(name = "circuitBreaker.enabled",value = "true"),                               // 是否开启断路器
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold",value = "10"),                  // 请求次数
            @HystrixProperty(name = "circuitBreaker.sleepWindowInMilliseconds",value = "10000"),            // 时间窗口期
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage",value = "60"),                // 失败率达到多少后跳闸
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds",value="3000")        // 超过 3 秒就降级熔断
    },threadPoolProperties = {
            @HystrixProperty(name = "coreSize",value = "10")                                                //最大的并发执行数量。默认10
    })
    public int updateUser(User user) {
        /*
            测试 Hystrix => 测试通过

            try{
                //睡 5 秒，必超时
                TimeUnit.MILLISECONDS.sleep(5000);
            }catch (Exception e){
                e.printStackTrace();
            }
         */

        //需要先对密码进行加密
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userMapper.updateById(user);
    }

    /**
     * 单位为 updateUser 方法定制的 fallback 方法
     * @param user
     * @return
     */
    public int updateUserFallback(User user){
        //TODO:有待改进
        System.out.println("请求超时或异常，进行降级熔断，user："+user);
        return 409;
    }

    /**
     * 该服务层统一的 fallback 方法 => 不能有参数
     * 只要其他方法上有加 @HystrixCommand 注解，当它们发送超时、异常和服务器宕机的时候，就会调用下面的方法
     *
     * @return 返回值类型必须是所有加上 @HystrixCommand 注解的方法的返回值类型或其子类，这里为了方便，这里就返回已经 User 进行消息提醒，实际中应想办法定义一个通用的类来返回
     * Fallback method 'public common.vo.common.ResponseBean com.hedon.service.impl.TestServiceImpl.globalFallbackMethod()' must return: class common.entity.User or its subclass
     */
    public User globalFallbackMethod(){
        //TODO:根据项目需求进行具体的熔断降级处理
        User user = new User();
        user.setId(4444444);
        user.setUsername("请求超时或异常");
        user.setPassword("请检查错误或稍后访问");
        return user;
    }
}
