import com.hedon.AuthCenterApplication;
import common.entity.DidaUser;
import common.mapper.DidaUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Hedon Wang
 * @create 2020-10-23 10:28
 */

@SpringBootTest(classes = {AuthCenterApplication.class})
public class DidaUserTest {

    @Autowired
    private DidaUserMapper didaUserMapper;

    @Test
    public void testGetUserByPhone(){
        DidaUser user = didaUserMapper.getUserByPhoneOrEmail("15623205156");
        System.out.println(user);
    }
}
