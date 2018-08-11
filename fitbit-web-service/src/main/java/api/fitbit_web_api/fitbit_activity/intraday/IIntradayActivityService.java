package api.fitbit_web_api.fitbit_activity.intraday;

import api.fitbit_web_api.fitbit_activity.constants.ActivitiesResource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;

public interface IIntradayActivityService<T extends AbstractIntradayActivity> {
    public Iterable<T> list();
    public Iterable<T> list(Pageable page);
    public Optional<T> getById(Long id);
    public Iterable<T> getByFitbitUserId(Long id);
    public Integer save(Iterable<T> entities);
    public Iterable<T> create(ArrayNode json);
    public T jsonToPOJO(Long fitbitUserId, Long dateTime, Double value, JsonNode node);
    Page<T> findAll(Specification<T> specs, Pageable pageble);
    //    public Iterable<T> list(Long fitbitUserId, ActivitiesResource resource, String from, String to);


}
