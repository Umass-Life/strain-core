package api.location;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LocationController {
    @RequestMapping(value = {"", "/"}, method= RequestMethod.GET)
    public ResponseEntity<Map> list(@RequestParam(value="fid",required=false) String fid,
                                    @RequestParam(value="sid",required=false) String sid){
        Map<String, Object> resMap = new HashMap<>();
        
        try  {
            return ResponseEntity.ok(resMap);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

}
