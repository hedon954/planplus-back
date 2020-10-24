package common.vo.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DidaUserRequestVo implements Serializable {

    private String userNickname;

    private LocalDate userBirthday;

    private Integer userGender;

    private Integer userId;



}
