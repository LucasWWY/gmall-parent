import com.example.gmall.feign.ware.WareFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author lfy
 * @Description
 * @create 2022-12-21 15:39
 */
@SpringBootTest
public class FeignTest {


    @Autowired
    WareFeignClient wareFeignClient;

    @Test
    void feignTest(){
        String s = wareFeignClient.hasStock(43L, 3);
        System.out.println(s);
    }
}
