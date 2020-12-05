package common.mapper;

import common.entity.DidaUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import common.vo.common.ResponseBean;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Results;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
public interface DidaUserMapper extends BaseMapper<DidaUser> {

    /**
     * 根据手机（用户名）获取用户信息
     *
     * @param username 用户名：手机或者邮箱
     * @return         用户信息
     */
    DidaUser getUserByPhoneOrEmail(String username);

    /**
     * 在登录的时候根据 UnionId 获取用户信息，没有的话就登录失败，不进行自动注册
     *
     * @param userUnionId
     * @return
     */
    DidaUser getUserByUnionIdWhenLogin(String userUnionId);

}
