package common.vo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;

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
}
