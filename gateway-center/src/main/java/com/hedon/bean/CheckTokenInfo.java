package com.hedon.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

/**
 * @author Hedon Wang
 * @create 2020-10-16 22:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CheckTokenInfo {

    /**
     * =================================================
     *     必须按照以下字段进行命名，OAuth2.0 才能精准封装
     * =================================================
     */
    private boolean active;       //令牌是否可用
    private String client_id;     //令牌是发给哪个客户端应用的
    private String[] scope;       //令牌拥有的对资源服务器的操作权限
    private String user_name;     //令牌发送给哪个用户
    private String[] aud;         //令牌可以访问哪些资源服务器
    private Date exp;             //令牌过期时间
    private String[] authorities; //令牌拥有的所有权限信息

}
