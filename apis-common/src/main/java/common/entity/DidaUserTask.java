package common.entity;

import com.baomidou.mybatisplus.annotation.*;

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
@TableName("tbl_dida_user_task")
@ApiModel(value="DidaUserTask对象", description="")
public class DidaUserTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_task_id", type = IdType.AUTO)
    private Integer userTaskId;

    private Integer didaUserId;

    @TableField("dida_task_Id")
    private Integer didaTaskId;

    @TableField("is_deleted")
    @TableLogic
    private Integer deleted;


}
