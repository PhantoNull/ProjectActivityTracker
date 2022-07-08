package eu.Rationence.pat.service;

import eu.Rationence.pat.model.ActivityType;
import eu.Rationence.pat.repository.ActivityTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityTypeService {
    private final ActivityTypeRepository activityTypeRepository;
    @Autowired
    public ActivityTypeService(ActivityTypeRepository activityTypeRepository) {
        this.activityTypeRepository = activityTypeRepository;
    }

    public ActivityType find(String activityType){return activityTypeRepository.getActivityTypeByActivityTypeKey(activityType);
    }
    public List<ActivityType> findAll() {
        return activityTypeRepository.findAll();
    }

    public ActivityType save(ActivityType a){ return activityTypeRepository.save(a);}

    public void delete(String activityType){ activityTypeRepository.deleteActivityTypeByActivityTypeKey(activityType);}
}
