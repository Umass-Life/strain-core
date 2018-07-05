package api.FitbitAPITest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@Controller("FitbitAPITest")
public class FitbitAPICallbackTest {

    @Test
    public void test(){

    }

    @RequestMapping(value = "/callback_test", method= RequestMethod.GET)
    public ResponseEntity<Map> callback_test(HttpServletRequest req){
        Map<String, Object> json = new HashMap<>();
        json.put("done", "1");
        return ResponseEntity.ok(json);
    }

}
