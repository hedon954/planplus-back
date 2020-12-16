import com.hedon.NotificationApplication;
import com.hedon.service.INotificationService;
import com.hedon.service.impl.NotificationServiceImpl;
import common.entity.DidaTask;
import common.mapper.DidaTaskMapper;
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

    @Autowired
    private NotificationServiceImpl notificationServiceImpl;

    @Autowired
    DidaTaskMapper didaTaskMapper;

    @org.junit.jupiter.api.Test
    public void test(){

        DidaTask didaTask = didaTaskMapper.selectById(440);

        String s = notificationServiceImpl.objectToJsonStr(didaTask, 2);
        System.out.println(s);

    }

}
