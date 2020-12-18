package common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 传送消息时封装的消息通知对象，包含需要的字段
 *
 * 注意点：不支持 LocalDateTime
 *
 * @author Hedon Wang
 * @create 2020-11-06 10:30
 */

@Data
@AllArgsConstructor
@ToString
public class TaskNotificationDto implements Serializable {

    /**
     * Data: 任务ID
     */
    private Integer taskId;

    /**
     * 延迟时间 = 当前时间 - 任务开始时间
     */
    private Long expiration;

    /**
     * 访问百度官方接口需要的令牌
     */
    private String accessToken;

    /**
     * 获取 token 的时间戳（相对于 UTC）
     */
    private Long tokenTime;

    /**
     * 消息模板ID
     */
    private String templateId;

    /**
     * 百度用户的 openID
     */
    private String touserOpenId;

    /**
     * 表单(场景)ID,一次订阅对应一个
     */
    private String sceneId;

    /**
     * 场景类型，固定为1
     */
    private Integer sceneType;

    /**
     * 点击消息后要跳转的页面
     */
    private String page;

    /**
     * 无参构造
     */
    public TaskNotificationDto(){
        this.templateId = "46af47b1cc8d40248bcf73ba8b3c76e4";
        this.sceneType = 1;
    }

    /**
     * 判断 token 是否过期
     * @return true：过期；false：没过期
     */
    public boolean tokenIsExpired(){
        if (accessToken == null){
            return false;
        }
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        return (now - tokenTime) >= 259000;
    }
}
