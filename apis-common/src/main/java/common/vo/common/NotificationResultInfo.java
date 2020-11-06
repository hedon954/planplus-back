package common.vo.common;

import io.swagger.models.auth.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Map;

/**
 * 发送消息通知的结果封装类
 *
 * @author Hedon Wang
 * @create 2020-11-06 11:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationResultInfo {

    private Integer errno;

    private String msg;

    private Map<String,Object> data;

}
