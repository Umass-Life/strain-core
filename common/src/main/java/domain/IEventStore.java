package domain;

import java.util.List;

public interface IEventStore {
   void store(Long aggregateId, List<Event> events);
   void load(Long aggregateId);
}
