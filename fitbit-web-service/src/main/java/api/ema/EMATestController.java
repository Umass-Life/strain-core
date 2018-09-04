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
        try{
            System.out.println(node);
        } catch(Exception e){

        }

        return ResponseEntity.ok("success");
    }
}

/*

{
  "msgtype": "batchslice",
  "id": "1534219271888",
  "type": "stress_level",
  "data": [
    {
      "timestamp": 1534219234,
      "level": "Neither"
    }
  ],
  "slice": 0,
  "slices": 1,
  "type_count": [
    2,
    3
  ]
}



* */
