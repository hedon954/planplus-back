package common.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 注册请求封装体
 *
 * @author Hedon Wang
 * @create 2020-11-26 11:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RegisterRequestVo implements Serializable {

    private String username;
    private String password;
    private String code;

}
