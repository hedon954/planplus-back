import com.hedon.ProjectApplication;
import com.hedon.service.IDidaUserService;
import common.entity.DidaUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;

/**
 * @author Hedon Wang
 * @create 2020-10-16 11:35
 */
@SpringBootTest(classes = {ProjectApplication.class})
public class MyBatisPlusTest {

    //测试跨模块能否扫描到 Mapper => 已测试通过
    @Autowired
    IDidaUserService didaUserService;

    //测试能否注入数据源 => 已测试通过
    @Autowired
    DataSource dataSource;

    @Test
    public void test(){
        System.out.println("dataSource:" + dataSource);
        DidaUser didaUser = didaUserService.getById(1);
        System.out.println(didaUser);
    }
}
