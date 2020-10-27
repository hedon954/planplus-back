package common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import common.entity.DidaTask;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
public interface DidaTaskMapper extends BaseMapper<DidaTask> {

    //创建新任务
    void insertTask(DidaTask didaTask);

    //按日期查询待办任务
    ArrayList<DidaTask> selectByDate(@Param("userId") Integer userId, @Param("date") String date);
}
