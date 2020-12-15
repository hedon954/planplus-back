import com.hedon.NotificationApplication;
import com.hedon.service.INotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Hedon Wang
 * @create 2020-11-25 23:41
 */
@SpringBootTest(classes = {NotificationApplication.class})
public class Test {

    @Autowired
    private INotificationService notificationService;

    @org.junit.jupiter.api.Test
    public void test(){
        notificationService.handleTimedTaskStartRemind();
    }

}
