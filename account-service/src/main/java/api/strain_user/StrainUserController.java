package api.strain_user;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collection;

@Controller
@RequestMapping(value = "/strain-user")
public class StrainUserController {

    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)
    public ResponseEntity create(){

        return ResponseEntity.ok("");
    }

    @RequestMapping(value = {"/", ""}, method = RequestMethod.GET)
    public ResponseEntity<Collection> list(){
        Collection l = new ArrayList<>();
        return ResponseEntity.ok(l);
    }
}
