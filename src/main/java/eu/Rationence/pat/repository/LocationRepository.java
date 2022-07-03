package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface LocationRepository extends CrudRepository<Location, String> {
    Location getLocationByLocationName(String locationName);
    List<Location> findAll();
    Location save(Location team);
    @Transactional
    void deleteLocationByLocationName(String locationName);
}


