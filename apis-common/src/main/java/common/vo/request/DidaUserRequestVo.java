package common.vo.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 前端传来的的用户信息封装类
 *
 * @author yang jie
 * @create 2020-10-24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DidaUserRequestVo implements Serializable {

    private String userNickname;

    private LocalDateTime userBirthday;

    private Integer userGender;

    private Integer userId;



}
