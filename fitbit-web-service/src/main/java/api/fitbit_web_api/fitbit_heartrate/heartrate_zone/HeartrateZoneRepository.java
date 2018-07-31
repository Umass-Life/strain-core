package api.fitbit_web_api.fitbit_heartrate.heartrate_zone;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface HeartrateZoneRepository extends PagingAndSortingRepository<HeartrateZone, Long> {
    Iterable<HeartrateZone> findByFitbitHeartrateId(Long id);

}
