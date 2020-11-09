package common.code;

import lombok.Getter;

/**
 * 返回码
 *
 * @author Hedon Wang
 * @create 2020-10-15 21:59
 */
@Getter
public enum ResultCode {


    /**
     * 默认成功值
     */
    SUCCESS(1000L,"操作成功"),

    /**
     * 通用错误
     */
    FAILED(1001L, "响应失败"),
    VALIDATE_FAILED(1002L, "参数校验失败"),
    ERROR_TIMESTAMP(1003L, "时间戳不合法"),
    ERROR_DECODE(1004L, "加密信息错误"),
    ERROR(5000L, "未知错误"),
    TIMEOUT(408L, "服务器繁忙"),
    FALLBACK(409L,"超时或者请求异常，进行服务降级和服务熔断"),

    /**
     * 认证模块错误码
     */
    ERROR_PASSWORD(10005L, "密码错误"),
    MISS_TOKEN(40000L, "缺少token"),
    ERROR_TOKEN_TYPE(40001L, "token类型错误"),
    ERROR_TOKEN_FORMAT(40002L, "token格式错误"),
    ERROR_TOKEN_VALUE(40003L, "token错误"),
    UNUSABLE_TOKEN(40004L, "refresh_token不能用来访问资源"),
    INVALID_AUTH(40100L, "未授权"),
    EXPIRED_ACCESS_TOKEN(40101L, "access_token过期"),
    EXPIRED_REFRESH_TOKEN(40102L, "refresh_token过期"),
    INVALID_ACCESS_TOKEN(40103L, "access_token失效"),
    INVALID_REFRESH_TOKEN(40104L, "refresh_token失效"),
    INVALID_APP_KEY(40105L, "appKey失效"),
    ERROR_CLIENT(40300L, "basic认证无效"),
    INSUFFICIENT_PERMISSION(40301L, "权限不足"),
    ERROR_SIGNING(40302L, "签名有误"),
    ERROR_SIGNIN(40303L, "登陆失败"),
    NO_LOGIN_IN(40304L,"未登录"),
    TO_MANY_REQUESTS(40305L,"请求过多"),
    NO_AUTHENTICATION_CODE(40306L,"小程序的 swan.login 没有成功传 code"),


    /**
     * 数据库操作相关错误码
     */
    DATABASE_ERROR(801L, "数据库操作异常"),
    PARAMETER_ERROR(804L, "参数错误"),
    INVALID_PARAMETER(805L, "不合法的参数"),
    MISS_PARAMETER(806L, "缺少参数"),
    REPEAT_RECORD(807L,"重复记录"),


    /**
     * 用户相关的错误码
     */
    EMPTY_USER_ID(50001L,"用户ID不能为空"),
    USER_NOT_EXIST(50002L,"用户不存在"),
    PHONE_FORMAT_ERROR(50003L,"手机号格式不正确"),
    EMPTY_PASSWORD(50004L,"密码不能为空"),
    GET_OPENID_FAILED(50005L,"获取 openId 和 sessionKey 错误"),


    /**
     * ==============================================
     *            可以自己再自定义一些新的错误码
     * ==============================================
     */

    /**
     * 任务相关的错误码
     */
    EMPTY_TASK_ID(60001L, "任务ID不能为空"),
    TASK_NOT_EXIST(60002L, "任务不存在"),
    USER_TASK_MISMATCHING(60003L, "用户和任务不匹配"),
    TIMED_TASK_CREATE_FAILED(60004L,"定时任务创建失败"),
    TASK_TO_FAR(60005L,"创建任务失败，只能创建未来一百天内的任务")


    ;


    private Long code;    //返回码
    private String msg;   //返回信息

    ResultCode(Long code,String msg){
        this.code = code;
        this.msg = msg;
    }

}
