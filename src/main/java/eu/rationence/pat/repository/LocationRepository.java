package eu.rationence.pat.repository;

import eu.rationence.pat.model.Location;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface LocationRepository extends CrudRepository<Location, String> {
    Location getLocationByLocationName(String locationName);

    List<Location> findAll();

    <S extends Location> S save(S location);

    @Transactional
    void deleteLocationByLocationName(String locationName);
}


