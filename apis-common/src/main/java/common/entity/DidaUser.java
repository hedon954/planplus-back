package common.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

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
@TableName("tbl_dida_user")
@ApiModel(value="DidaUser对象", description="")
public class DidaUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    private String userPhone;

    private String userNickname;

    private String userPassword;

    private Integer userGender;

    private LocalDateTime userBirthday;

    @TableField("is_deleted")
    @TableLogic
    private Integer dDeleted;

    private String userAvatarUrl;


}
