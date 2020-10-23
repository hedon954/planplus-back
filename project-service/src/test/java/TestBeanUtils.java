import com.hedon.ProjectApplication;
import com.hedon.service.IDidaUserService;
import common.entity.DidaUser;
import common.vo.response.DidaUserResponseVo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Hedon Wang
 * @create 2020-10-23 17:08
 */
@SpringBootTest(classes = {ProjectApplication.class})
public class TestBeanUtils {


    @Autowired
    IDidaUserService didaUserService;

    @Test
    public void testUser(){

        DidaUser userById = didaUserService.getUserById(1);
        System.out.println(userById);
        DidaUserResponseVo didaUserResponseVo = new DidaUserResponseVo(userById);
        System.out.println(didaUserResponseVo);
    }

}
