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
 * @since 2020-11-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_verification_code")
@ApiModel(value="VerificationCode对象", description="")
public class VerificationCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "code_id", type = IdType.AUTO)
    private Integer codeId;

    private String codeNumber;

    private String codeUsername;

    @TableField("is_active")
    private Integer isActive;

    @TableField("is_deleted")
    @TableLogic
    private Integer deleted;


}
