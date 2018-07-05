package api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class TestController {
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public ResponseEntity ping(){
        return ResponseEntity.ok("pong");
    }
}
