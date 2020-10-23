package common.vo.response;

import common.entity.DidaUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.beans.BeanUtils;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 返回给前端的用户信息封装类
 *
 * 不返回密码
 *
 * @author Hedon Wang
 * @create 2020-10-23 17:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DidaUserResponseVo implements Serializable {

    private Integer userId;

    private String userPhone;

    private String userNickname;

    private Integer userGender;

    private LocalDateTime userBirthday;

    private String userAvatarUrl;

    /**
     * 通过 DidaUser 对象来构造 DidaUserResponseVo
     * @param didaUser
     */
    public DidaUserResponseVo(DidaUser didaUser){
        //可以直接使用 spring 自带的 BeanUtils 工具类来克隆属性
        BeanUtils.copyProperties(didaUser,this);
        /*this.userId = didaUser.getUserId();
        this.userPhone = didaUser.getUserPhone();
        this.userNickname = didaUser.getUserNickname();
        this.userGender = didaUser.getUserGender();
        this.userBirthday = didaUser.getUserBirthday();
        this.userAvatarUrl = didaUser.getUserAvatarUrl();*/
    }
}
