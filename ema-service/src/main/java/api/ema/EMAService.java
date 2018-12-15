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
public class EMAService {
    private static final Logger logger = Logger.getLogger(StressLevelService.class.getSimpleName());
    private static final ColorLogger colorLog = new ColorLogger(logger);

    @Autowired
    private EMARepository repository;

    public Iterable<StressLevel> list(){
        Page page = repository.findAll(PageRequest.of(0, Integer.MAX_VALUE, Sort.Direction.DESC, "dateTime"));
        return page.getContent();
    }

    public Optional<EMA> getById(Long id){
        return repository.findById(id);
    }

    public List<EMA> create(JsonNode node) {
        final String TIMESTAMP = "timestamp";
        final String LEVEL = "level";
        final String TYPE = "type";
        final String STRAIN_ID = "strainId";
        final String FITBIT_ID = "fitbitId";

        if (!node.has("msgtype") && !node.has("data")) {
            throw new IllegalArgumentException("wrong json type: \n" + node);
        }

        if (!node.has(STRAIN_ID)){
            throw new IllegalArgumentException("strain-id not found");
        }
        if (!node.has(FITBIT_ID)){
            throw new IllegalArgumentException("fitbit-id not found");
        }

        String strainIdString = node.get(STRAIN_ID).asText();
        Long strainId = Long.parseLong(strainIdString);
        String fitbitId = node.get(FITBIT_ID).asText();

        ArrayNode data = (ArrayNode) node.get("data");
        if (data== null) throw new IllegalArgumentException("json needs to have field \'data\':\n"+node);
        List<EMA> levels = new ArrayList<>();
        for(int i = 0; i < data.size(); i++){
            JsonNode data_i = data.get(i);
            try {
                if (!data_i.has(TIMESTAMP)){
                    throw new IllegalArgumentException("bad ema json datum: " + data_i.toString());
                }

                // parse data.
                Long dateTime = data_i.get(TIMESTAMP).asLong();
                JsonNode stressJson = data_i.get("stress");
                JsonNode moodJson = data_i.get("mood");
                JsonNode painJson= data_i.get("pain");

                String stressValue = stressJson.get(LEVEL).asText();
                String stressType = stressJson.get(TYPE).asText();

                String moodValue = moodJson.get(LEVEL).asText();
                String moodType = moodJson.get(TYPE).asText();

                String painValue = painJson.get(LEVEL).asText();
                String painType = painJson.get(TYPE).asText();

                colorLog.info(stressJson);
                colorLog.info(moodJson);
                colorLog.info(painJson);

                EMA ema1 = create(strainId, fitbitId, dateTime, stressType, stressValue);
                EMA ema2 = create(strainId, fitbitId, dateTime, moodType, moodValue);
                EMA ema3 = create(strainId, fitbitId, dateTime, painType, painValue);
                levels.add(ema1);
                levels.add(ema2);
                levels.add(ema3);
            } catch (Exception e){
                colorLog.severe(e.getMessage());
            }
        }
        return levels;
    }




    public EMA create(Long strainId, String fitbitId, Long dateTime, String emaType, String emaValue){
        EMA ema = new EMA(strainId, fitbitId, dateTime, emaType, emaValue);
        return repository.save(ema);
    }


}