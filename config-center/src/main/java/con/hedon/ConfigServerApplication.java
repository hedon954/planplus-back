package con.hedon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.config.server.EnableConfigServer;

/**
 * @author Hedon Wang
 * @create 2020-10-15 15:20
 */
@SpringBootApplication
@EnableConfigServer         //作为配置中心
@EnableDiscoveryClient      //注册到 Consul 中
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class,args);
    }
}
