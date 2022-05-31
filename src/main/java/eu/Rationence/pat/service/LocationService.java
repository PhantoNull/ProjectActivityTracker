package eu.Rationence.pat.service;

import eu.Rationence.pat.model.Location;
import eu.Rationence.pat.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class LocationService {
    @Autowired
    private LocationRepository locationRepository;

    public Location find(String locationName){return locationRepository.getLocationByLocationName(locationName);
    }
    public List<Location> findAll() { return locationRepository.findAll();}

    public Location save(Location location){ return locationRepository.save(location);}

    public void delete(String locationName){locationRepository.deleteLocationByLocationName(locationName);}
}
