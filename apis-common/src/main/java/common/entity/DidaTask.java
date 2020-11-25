package common.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_dida_task")
@ApiModel(value="DidaTask对象", description="")
public class DidaTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "task_Id", type = IdType.AUTO)
    private Integer taskId;

    private String taskContent;

    private String taskPlace;

    private Integer taskRate;

    private LocalDateTime taskStartTime;

    private LocalDateTime taskPredictedFinishTime;

    private LocalDateTime taskRealFinishTime;

    private Integer taskAdvanceRemindTime;

    private String taskConsumedTime;

    private LocalDateTime taskRemindTime;

    private Integer taskStatus;

    @TableField("is_deleted")
    @TableLogic
    private Integer deleted;

    private Integer taskFinishPercent;

    private String taskFormId;

    public DidaTask(){
        this.taskRate = 0;
        this.taskAdvanceRemindTime = 5;
        this.taskStatus = 0;
        this.taskFinishPercent = 0;
    }

}
