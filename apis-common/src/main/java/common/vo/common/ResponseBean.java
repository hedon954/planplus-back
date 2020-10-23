package common.vo.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import common.code.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 通用的返回给前端的对象模板
 *
 * @author Hedon Wang
 * @create 2020-10-15 22:15
 */
@Data
@ToString
@NoArgsConstructor
public class ResponseBean<T> implements Serializable {

    //是否成功
    private Integer success;

    //返回码
    private Long code;

    //返回信息
    private String message;

    //返回数据
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    //成功 —— 带数据
    public static <TData> ResponseBean<TData> success(TData data){
        return new ResponseBean<>(1,ResultCode.SUCCESS,data);
    }

    //成功 —— 无数据
    public static <TData> ResponseBean<TData> success(){
        return success(null);
    }

    //失败 —— 错误码版本
    public static ResponseBean fail(ResultCode resultCode){
        return new ResponseBean(0,resultCode,null);
    }

    //失败  —— 纯自定义
    public static ResponseBean fail(Long code, String message){
        return new ResponseBean(0,code,message,null);
    }

    //失败 —— 错误码 + 自定义信息
    public static ResponseBean fail(ResultCode resultCode,String message){
        return new ResponseBean(0,resultCode.getCode(),message,null);
    }

    /**
     * 构造方法
     * @param success     是否成功 => 1：成功；0：失败
     * @param resultCode  返回码及信息
     * @param data        携带的数据
     */
    public ResponseBean(Integer success, ResultCode resultCode, T data) {
        this.success = success;
        this.code = resultCode.getCode();
        this.message = resultCode.getMsg();
        this.data = data;
    }

    /**
     * 构造方法 -> 通用版
     * @param success       是否成功 => 1：成功；0：失败
     * @param code          返回码
     * @param message       返回信息
     * @param data          返回数据
     */
    public ResponseBean(Integer success, Long code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }
}
