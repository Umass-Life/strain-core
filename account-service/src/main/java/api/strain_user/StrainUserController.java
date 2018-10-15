package api.strain_user;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import util.ColorLogger;

import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/strain-user")
public class StrainUserController {
    private Logger log = Logger.getLogger(StrainUserController.class.getSimpleName());
    private ColorLogger colorLogger = new ColorLogger(log);

    @Autowired
    private StrainUserService userService;

    @Value("@{view.uri}")
    private String WEBAPP_HOME;

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<Map> list(@RequestParam(value="id",required=false) Long id,
                                    @RequestParam(value="email",required=false) String email){
        Map<String, Object> res = new HashMap<>();
        try {
            if (id == null && (email == null || email.isEmpty())){
                Iterable<StrainUser> users = userService.list();
                res.put(StrainUser.PLIURAL, users);
            } else {
                Optional<StrainUser> userOpt = null;
                if (id != null) {
                    userOpt = userService.getById(id);
                }
                if (email != null || !email.isEmpty()) {
                    userOpt = userService.getByEmail(email);
                }
                if (!userOpt.isPresent()) {
                    throw new IllegalArgumentException(
                            String.format("Cannot find userOpt with queries: id=%s email=%s\n", id, email));
                }
                res.put(StrainUser.SINGULAR, userOpt.get());
            }
            return ResponseEntity.ok(res);
        } catch(Exception e){
            return buildResponseOnException(e);
        }

    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public ResponseEntity create(@RequestBody JsonNode body){
        Map<String, Object> responseMap = new HashMap<>();
        try {
            colorLogger.info("creating: " + body.toString());
            String email    = body.get("email").asText();

            String password = body.get("password").asText();
            StrainUser user = userService.createWithUniqueEmail(email, password);

            colorLogger.info("created: " + user);
            responseMap.put(StrainUser.SINGULAR, user);
            return ResponseEntity.ok(responseMap);
        } catch (Exception e){
            return buildResponseOnException(e);

        }

    }

    private ResponseEntity<Map> buildResponseOnException(Exception e){
        e.printStackTrace();
        colorLogger.severe(e.getMessage());
        HashMap res = new HashMap<>();
        res.put("error", e.getMessage());
        return ResponseEntity.badRequest().body(res);
    }


}
