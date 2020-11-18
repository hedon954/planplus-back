package common.vo.request;

import common.entity.DidaTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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

    @NotBlank(message = "任务内容不得为空")
    private String taskContent;

    private String taskPlace;

    private Integer taskRate;

    @NotNull(message = "任务开始时间不得为空")
    private LocalDateTime taskStartTime;

    private LocalDateTime taskPredictedFinishTime;

    private LocalDateTime taskRealFinishTime;

    private Integer taskAdvanceRemindTime;

    private String taskConsumedTime;

    private Integer taskStatus;

    private Integer taskFinishPercent;

    /**
     * 订阅通知信息的表单ID，每条任务对应一个
     */
    private String taskFormId;

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
