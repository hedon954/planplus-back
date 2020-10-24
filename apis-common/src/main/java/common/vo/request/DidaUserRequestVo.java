package common.vo.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
