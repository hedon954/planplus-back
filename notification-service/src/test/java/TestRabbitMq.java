/**
 * @author Hedon Wang
 * @create 2020-11-05 23:55
 */

import com.hedon.NotificationApplication;
import com.hedon.message.DeadInfo;
import com.hedon.rabbitmq.TestPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {NotificationApplication.class})
public class TestRabbitMq {

    @Autowired
    private TestPublisher publisher;

    @Test
    public void testRabbitMq(){
        DeadInfo deadInfo = new DeadInfo(10086,"你好，这是死信队列");
        publisher.sendDeadMsg(deadInfo);
    }

}
