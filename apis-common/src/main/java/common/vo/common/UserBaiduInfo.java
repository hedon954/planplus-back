package common.vo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;
import java.util.Map;

/**
 * 用来存放百度用户的 openId 和 sessionKey
 *
 * @author Hedon Wang
 * @create 2020-11-03 22:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserBaiduInfo implements Serializable {

    private String openid;
    private String session_key;

    /**
     * data 中存放着 unionId
     */
    private Map<String,String> data;

    /**
     * 错误码，0表示正常
     */
    private Integer errno;
}
