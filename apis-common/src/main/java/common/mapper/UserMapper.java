package common.mapper;

import common.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-16
 */
public interface UserMapper extends BaseMapper<User> {

    User getUserByUsername(String username);

}
