package common.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
public class RegisterRequestVo {

    private String phoneNumber;
    private String password;

}
