package common.mapper;

import common.entity.DidaUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import common.vo.request.DidaUserRequestVo;

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

    //根据requestVo修改用户信息
    void updateUserByVo(DidaUserRequestVo requestVo);
}
