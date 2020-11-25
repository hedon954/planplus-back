package common.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.time.ZoneOffset;

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


    /**
     * 判断 token 是否过期
     * @return true：过期；false：没过期
     */
    public boolean tokenIsExpired(){
        if (accessToken == null){
            return false;
        }
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        long tokenTimeSecond = tokenTime.toEpochSecond(ZoneOffset.UTC);
        return (now - tokenTimeSecond) >= 259000;
    }

}
