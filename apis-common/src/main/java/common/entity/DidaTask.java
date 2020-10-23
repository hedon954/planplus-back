package common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    private LocalDateTime taskPredictedFinshTime;

    private LocalDateTime taskRealFinishTime;

    private Integer taskAdvanceRemindTime;

    private String taskConsumedTime;

    private Integer taskStatus;

    @TableField("is_deleted")
    @TableLogic
    private Integer deleted;

    private Integer taskFinishPercent;


}
