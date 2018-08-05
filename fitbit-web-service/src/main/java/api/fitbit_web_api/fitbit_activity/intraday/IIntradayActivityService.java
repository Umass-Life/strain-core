package api.fitbit_web_api.fitbit_activity.intraday;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IIntradayActivityService<T extends AbstractIntradayActivity> {
    public Iterable<T> list();
    public Iterable<T> list(Pageable page);
    public Optional<T> getById(Long id);
    public Iterable<T> getByFitbitUserId(Long id);
    public Iterable<T> save(Iterable<T> entities);
    public Iterable<T> create(ArrayNode json);
    public T jsonToPOJO(Long fitbitUserId, Long dateTime, Double value, JsonNode node);

}
