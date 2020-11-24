package common.vo.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 根据一句话创建任务的封装类
 *
 * @author Hedon Wang
 * @create 2020-11-24 10:41
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DidaTaskSentenceRequestVo {

    /**
     * 任务信息
     */
    private String taskInfo;

    /**
     * 订阅通知信息的表单ID，每条任务对应一个
     */
    private String taskFormId;


}
