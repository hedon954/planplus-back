package common.vo.response;

import common.entity.DidaTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 封装的任务信息，仅包含未完成任务所必需的字段
 *
 * @author yang jie
 * @create 2020-10-26 22:10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DidaTaskResponseVo implements Serializable {

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

    public DidaTaskResponseVo(DidaTask didaTask) {
        BeanUtils.copyProperties(didaTask, this);
    }
}
