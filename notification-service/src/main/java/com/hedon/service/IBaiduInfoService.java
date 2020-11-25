package com.hedon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import common.entity.BaiduInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-11-06
 */
public interface IBaiduInfoService extends IService<BaiduInfo> {

    BaiduInfo getPlanPlusInfo();


}
