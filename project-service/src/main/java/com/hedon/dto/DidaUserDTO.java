package com.hedon.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Ruolin
 */
@Data
public class DidaUserDTO {
    private Integer userId;

    private String userNickname;

    private Integer userGender;

    private LocalDateTime userBirthday;
}
