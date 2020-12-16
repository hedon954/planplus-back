package common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import common.entity.DidaTask;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
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

//   // 创建新任务
//    void insertTask(DidaTask didaTask);

    //按日期查询待办任务
    ArrayList<DidaTask> selectByDate(@Param("userId") Integer userId, @Param("date") String date);

    //按状态查询任务
    ArrayList<DidaTask> selectByStatus(@Param("userId") Integer userId, @Param("taskStatus") Integer taskStatus);

    //查询所有任务
    ArrayList<DidaTask> selectAll(Integer userId);

    //按照日期区间查询任务
    ArrayList<DidaTask> selectTasksByTimeRegion(@Param("userId") Integer userId, @Param("startTime") String startTime, @Param("finishTime") String finishTime);
}
