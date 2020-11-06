package common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2020-11-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_baidu_info")
@ApiModel(value="BaiduInfo对象", description="")
public class BaiduInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private String accessToken;

    private LocalDateTime tokenTime;


}
