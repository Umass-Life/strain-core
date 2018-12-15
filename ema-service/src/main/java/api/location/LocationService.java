package api.location;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    @Autowired
    private LocationRepository repository;
    public Iterable<Location> list(){
        return repository.findAll();
    }

    public Iterable<Location> listByFitbitId(String fitbitId){
        return null;
    }

    public Iterable<Location> listByStrainId(Long strainId){
        return null;
    }

    public Iterable<Location> createInBulk(JsonNode root){
        return null;
    }
}
