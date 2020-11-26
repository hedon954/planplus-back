package common.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Hedon Wang
 * @create 2020-11-26 19:25
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FeedbackRequestVo implements Serializable {

    /**
     * 反馈者邮件
     */
    private String email;

    /**
     * 反馈信息
     */
    private String content;

}
