package api.ema;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/ema")
public class EMATestController {
    @RequestMapping(value = {"/",""}, method = RequestMethod.GET)
    public ResponseEntity get(){
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("hello", "from server");
        System.out.println("Hello from fitbit");
        return ResponseEntity.ok(jsonMap);
    }


    @RequestMapping(value = {"/",""}, method = RequestMethod.POST)
    public ResponseEntity post(@RequestBody JsonNode node){
        System.out.println(node);
        return ResponseEntity.ok("success");
    }
}
