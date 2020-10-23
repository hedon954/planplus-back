import com.hedon.ProjectApplication;
import common.entity.DidaUser;
import common.mapper.DidaUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Hedon Wang
 * @create 2020-10-23 09:54
 */
@SpringBootTest(classes = {ProjectApplication.class})
public class TestLogicDelete {

    @Autowired
    DidaUserMapper didaUserMapper;

    @Test
    public void testInsert(){
        DidaUser didaUser = new DidaUser();
        didaUser.setUserGender(1);
        didaUser.setUserPhone("15623205156");
        didaUser.setUserPassword("hedon");
        didaUser.setUserNickname("hedon");
        didaUserMapper.insert(didaUser);
    }

    @Test
    public void testDelete(){
        didaUserMapper.deleteById(1);
    }
}
