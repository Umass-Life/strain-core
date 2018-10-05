package api.fitbit_web_api.fitbit_api_query_ingestion;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/ingestion")
public class IngestionController {
    Logger logger = Logger.getLogger(IngestionController.class.getSimpleName());

    @RequestMapping(value={"/", ""}, method= RequestMethod.POST)
    public ResponseEntity test(@RequestBody Map<String, Object> map){
        try {
            return ResponseEntity.ok(map);
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("error");
        }
    }


}
