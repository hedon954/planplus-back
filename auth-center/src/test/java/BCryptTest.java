import com.hedon.AuthCenterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Hedon Wang
 * @create 2020-10-16 17:10
 */
@SpringBootTest(classes = {AuthCenterApplication.class})
public class BCryptTest {

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    public void test(){
        System.out.println(passwordEncoder.encode("123456"));
    }

}
