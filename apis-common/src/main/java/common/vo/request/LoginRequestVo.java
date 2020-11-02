package common.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 前端登录时传给后端的对象
 *
 * @author Hedon Wang
 * @create 2020-11-02 12:15
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginRequestVo implements Serializable {

    private String phoneNumber;
    private String password;

}
