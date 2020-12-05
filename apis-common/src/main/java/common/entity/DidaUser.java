package common.entity;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Random;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

/**
 * <p>
 * 
 * </p>
 *
 * @author Jiahan Wang
 * @since 2020-10-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tbl_dida_user")
@ApiModel(value="DidaUser对象", description="")
public class DidaUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    private String userPhone;

    private String userEmail;

    private String userNickname;

    private String userPassword;

    private Integer userGender;

    private LocalDateTime userBirthday;

    @TableField("is_deleted")
    @TableLogic
    private Integer deleted;

    private String userAvatarUrl;

    private String userOpenId;

    private String userSessionKey;

    private String userUnionId;

    public DidaUser(){
        this.userGender = 1;
        this.userBirthday = LocalDateTime.now();
        this.userNickname = "新用户";
        this.userAvatarUrl = assignAvatarRandom();
    }

    /**
     * 随机分配头像
     *
     * @return
     */
    private String assignAvatarRandom() {

        String[] avatars = new String[]{
            "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2364553516,415583389&fm=26&gp=0.jpg",
                "http://img22.mtime.cn/up/2011/01/26/133951.22923590_500.jpg",
                "http://img22.mtime.cn/up/2011/01/26/133948.20577146_500.jpg",
                "http://img22.mtime.cn/up/2011/01/26/133946.39385564_500.jpg",
                "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1412998848,651818686&fm=26&gp=0.jpg",
                "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=276601955,2473380832&fm=26&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2279757342,2988437734&fm=26&gp=0.jpg",
                "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2535040877,1808091952&fm=26&gp=0.jpg",
                "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=296380031,866571273&fm=26&gp=0.jpg",
                "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=2403683895,3880372056&fm=26&gp=0.jpg",
                "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1225099212,660758811&fm=26&gp=0.jpg",
                "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=3155860864,1692727412&fm=26&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=519221706,1897432054&fm=26&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=1600497847,2272524603&fm=26&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=419400804,285387934&fm=26&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2095813626,802270332&fm=26&gp=0.jpg",
                "https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=312551271,1808785806&fm=26&gp=0.jpg",
                "https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2793034585,1312561967&fm=26&gp=0.jpg",
                "https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3957272486,2748324463&fm=26&gp=0.jpg",
                ""
        };

        return avatars[RandomUtils.nextInt(0,avatars.length-1)];


    }


}
