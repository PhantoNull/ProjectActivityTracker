package eu.rationence.pat.service;

import eu.rationence.pat.model.Location;
import eu.rationence.pat.repository.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {
    private final LocationRepository locationRepository;

    @Autowired
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location find(String locationName){return locationRepository.getLocationByLocationName(locationName);
    }
    public List<Location> findAll() { return locationRepository.findAll();}

    public Location save(Location location){ return locationRepository.save(location);}

    public void delete(String locationName){locationRepository.deleteLocationByLocationName(locationName);}
}
