package com.hedon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.service.IBaiduInfoService;
import common.entity.BaiduInfo;
import common.mapper.BaiduInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-11-06
 */
@Service
@Slf4j
public class BaiduInfoServiceImpl extends ServiceImpl<BaiduInfoMapper, BaiduInfo> implements IBaiduInfoService {

    @Autowired
    private BaiduInfoMapper baiduInfoMapper;

    @Autowired
    private Environment environment;

    /**
     * 获取小程序 planplus 对应的百度信息
     * @return
     */
    @Override
    public BaiduInfo getPlanPlusInfo() {
        return baiduInfoMapper.selectById(environment.getProperty("baidu.planplus.app-id"));
    }



}
