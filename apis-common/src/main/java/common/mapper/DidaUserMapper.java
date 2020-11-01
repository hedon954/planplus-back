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

    //根据手机（用户名）获取用户信息
    DidaUser getUserByPhone(String user_name);

    /**
     * 更新用户信息
     * @param didaUser
     */
    @Results
    void updateUserInfo(DidaUser didaUser);

//    String getPsw(Integer user_id);
//
//    void updatePsw(Integer user_id,String new_psw);

}
