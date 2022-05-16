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

    public Location findLocationByLocationName(String string){return locationRepository.getLocationByLocationName(string);
    }
    public List<Location> findAll() { return locationRepository.findAll();}

    public Location saveLocation(Location location){ return locationRepository.save(location);}

    public void deleteLocationByLocationName(String locationName){locationRepository.deleteLocationByLocationName(locationName);}
}
