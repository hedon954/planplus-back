import com.hedon.AuthCenterApplication;
import common.entity.User;
import common.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Hedon Wang
 * @create 2020-10-16 17:20
 */
@SpringBootTest(classes = {AuthCenterApplication.class})
public class UserTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void testGetUserByUsername(){
        User hedon = userMapper.getUserByUsername("hedon");
        System.out.println(hedon);
    }
}
