package api.ema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import util.ColorLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class StressLevelService {
    private static final Logger logger = Logger.getLogger(StressLevelService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private StressLevelRepository repository;

    public Iterable<StressLevel> list(){
        Page page = repository.findAll(PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.DESC, "dateTime"));
        return page.getContent();
    }

    public Optional<StressLevel> getById(Long id){
        return repository.findById(id);
    }

    public List<StressLevel> create(JsonNode node) {
        final String TIMESTAMP = "timestamp";
        final String LEVEL = "level";
        final String STRAIN_ID = "strainid";
        final String FITBIT_ID = "fitbitId";

        if (!node.has("msgtype") && !node.has("data")) {
            throw new IllegalArgumentException("wrong json type: \n" + node);
        }

        ArrayNode data = (ArrayNode) node.get("data");
        if (data== null) throw new IllegalArgumentException("json needs to have field \'data\':\n"+node);
        List<StressLevel> levels = new ArrayList<>();
        for(int i = 0; i < data.size(); i++){
            JsonNode data_i = data.get(i);
            try {
                if (!data_i.has(TIMESTAMP) && !data_i.has(LEVEL)){
                    throw new IllegalArgumentException("bad stress json datum: " + data_i.toString());
                }
                if (!data.has(STRAIN_ID)){
                    throw new IllegalArgumentException("strain-id not found");
                }
                if (!data.has(FITBIT_ID)){
                    throw new IllegalArgumentException("fitbit-id not found");
                }
                // parse data.

                Long dateTime = data_i.get(TIMESTAMP).asLong();
                String level = data_i.get(LEVEL).asText();
                Long strainId = data_i.get(STRAIN_ID).asLong();
                String fitbitId = data_i.get(FITBIT_ID).asText();

                StressLevel stressLevel = create(strainId, fitbitId, dateTime, level);
                levels.add(stressLevel);
            } catch (Exception e){
                colorLog.severe(e.getMessage());
            }
        }
        return levels;
    }




    public StressLevel create(Long strainId, String fitbitId, Long dateTime, String stressLevel){
        StressLevel stressLevel1 = new StressLevel(strainId, fitbitId, dateTime, stressLevel);
        return repository.save(stressLevel1);
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
 */