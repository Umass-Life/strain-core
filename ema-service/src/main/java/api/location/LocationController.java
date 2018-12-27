package api.location;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import util.ColorLogger;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class LocationController {
    private static Logger logger = Logger.getLogger(LocationController.class.getSimpleName());
    private static ColorLogger colorLogger = new ColorLogger(logger);
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

    @RequestMapping(value = {"", "/"}, method = RequestMethod.POST)
    public ResponseEntity<Map> create(@RequestBody Map<String, Object> body){
        Map<String, Object> resMap = new HashMap<>();
        try {
            return ResponseEntity.ok(resMap);
        } catch (Exception e){
            e.printStackTrace();
            colorLogger.severe(e.getMessage());
            resMap.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(resMap);
        }
    }

}
