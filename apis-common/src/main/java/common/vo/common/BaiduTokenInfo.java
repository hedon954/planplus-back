package common.vo.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 百度的 Token 信息封装类
 *
 * @author Hedon Wang
 * @create 2020-11-06 11:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BaiduTokenInfo implements Serializable {

    private String  access_token;

    /*
    下面的都没什么用，先不封装
    private String session_key;
    private String scope;
    private String refresh_token;
    private String session_secret;
    private String expires_in;
     */

}
