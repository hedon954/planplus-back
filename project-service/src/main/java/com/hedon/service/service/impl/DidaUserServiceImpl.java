package com.hedon.service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hedon.service.service.IDidaUserService;
import common.entity.DidaUser;
import common.mapper.DidaUserMapper;
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
public class DidaUserServiceImpl extends ServiceImpl<DidaUserMapper, DidaUser> implements IDidaUserService {

}
