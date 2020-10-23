package common.exception;

import common.code.ResultCode;
import common.vo.common.ResponseBean;
import lombok.Data;

/**
 * service 服务层报错的时候抛出的异常
 *
 * @author Hedon Wang
 * @create 2020-10-15 22:11
 */
@Data
public class ServiceException extends RuntimeException {

    //抛出错误的时候携带错误码
    private ResultCode resultCode;

    //无参构造 => 未知错误
    public ServiceException() {
        this.resultCode = ResultCode.ERROR;
    }

    //有参构造 => 明确错误
    public ServiceException(ResultCode resultCode){
        this.resultCode = resultCode;
    }

    //有参构造 => 明确错误
    public ServiceException(String message, ResultCode resultCode) {
        super(message);
        this.resultCode = resultCode;
    }

    //从抛出的错误中获取错误响应
    public ResponseBean getFailResponse(){
        if (getMessage() != null && !getMessage().isEmpty()){
            return ResponseBean.fail(resultCode,getMessage());
        }
        return ResponseBean.fail(resultCode);
    }
}
