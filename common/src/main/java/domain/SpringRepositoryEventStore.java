package domain;

import org.springframework.data.repository.Repository;

import java.util.List;

public class SpringRepositoryEventStore<T, ID>  implements IEventStore{

    Repository<T, ID> repository;

    public SpringRepositoryEventStore(Repository<T, ID> repository){
        this.repository = repository;
    }

    @Override
    public void store(Long aggregateId, List<Event> events) {

    }

    @Override
    public void load(Long aggregateId) {

    }
}
