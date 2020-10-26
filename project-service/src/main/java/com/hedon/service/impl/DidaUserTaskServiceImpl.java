package com.hedon.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.service.IDidaUserTaskService;
import common.entity.DidaUserTask;
import common.mapper.DidaUserTaskMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@Service
public class DidaUserTaskServiceImpl extends ServiceImpl<DidaUserTaskMapper, DidaUserTask> implements IDidaUserTaskService {

    @Autowired
    DidaUserTaskMapper didaUserTaskMapper;

    //插入用户——任务
    @Override
    public void insertUserTask(DidaUserTask userTask) {
        didaUserTaskMapper.insert(userTask);
    }
}
