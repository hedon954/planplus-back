package common.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Hedon Wang
 * @create 2020-11-30 14:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangePwdRequestVo implements Serializable {
    private String username;
    private String password;
    private String code;
}
