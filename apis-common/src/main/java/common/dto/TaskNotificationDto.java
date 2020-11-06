package common.dto;

import javafx.concurrent.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 传送消息时封装的消息通知对象，包含需要的字段
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
     * 访问百度官方接口需要的令牌
     */
    private String accessToken;

    /**
     * 获取 token 的时间节点
     */
    private LocalDateTime tokenTime;

    /**
     * 消息模板ID
     */
    private String templateId;

    /**
     * 百度用户的 openID
     */
    private String touserOpenId;

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
        this.templateId = "480d2c84bfb74d25bf15171388625678";
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
        long getTokenTime = getTokenTime().toInstant(ZoneOffset.UTC).getEpochSecond();
        long now = LocalDateTime.now().toInstant(ZoneOffset.UTC).getEpochSecond();
        return (now - getTokenTime) >= 259000;
    }
}
