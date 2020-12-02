package com.hedon.sercurity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Hedon Wang
 * @create 2020-11-02 09:14
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TokenInfo implements Serializable {

    private String access_token;
    /**
     * 刷新令牌
     */
    private String refresh_token;
    private String token_type;
    private Long expires_in;
    private String scope;

    /**
     * 自定义的字段，记录过期时间
     */
    private LocalDateTime expiredTime;

    /**
     * 初始化方法：过期时间 = 当前时间 + token 有效期 - 3
     * 减掉 3 秒是为了防止误差
     */
    public TokenInfo init(){
        expiredTime = LocalDateTime.now().plusSeconds(expires_in - 3);
        return this;
    }

    /**
     * 判断 token 是否过期
     * @return
     */
    public boolean isExpired() {
        return expiredTime.isBefore(LocalDateTime.now());
    }
}
