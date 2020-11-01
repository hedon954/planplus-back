package common.vo.request;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import common.entity.DidaTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 前端传来的任务信息封装类
 *
 * @author yang jie
 * @create 2020-10-25 23:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DidaTaskRequestVo implements Serializable {

    private String taskContent;

    private String taskPlace;

    private Integer taskRate;

    private LocalDateTime taskStartTime;

    private LocalDateTime taskPredictedFinishTime;

    private LocalDateTime taskRealFinishTime;

    private Integer taskAdvanceRemindTime;

    private String taskConsumedTime;

    private Integer taskStatus;

    private Integer taskFinishPercent;

    /**
     * 将封装的任务信息转换成DidaTask对象
     *
     * @author yang jie
     * @create 2020-10-25 23:00
     * @param taskInfo
     * @return
     */
    public static DidaTask toDidaTask(DidaTaskRequestVo taskInfo) {
        DidaTask didaTask = new DidaTask();
        BeanUtils.copyProperties(taskInfo, didaTask);
        return didaTask;
    }
}
