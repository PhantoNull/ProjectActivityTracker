package eu.Rationence.pat.service;

import eu.Rationence.pat.model.Activity;
import eu.Rationence.pat.model.Project;
import eu.Rationence.pat.repository.ActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

    public Activity findActivityByActivityKeyAndProject(String string, String project){
        return activityRepository.getActivityByActivityKeyAndProject(string, project);
    }
    public List<Activity> findActivitiesByProject(String project) {
        return activityRepository.findActivitiesByProject(project);
    }

    public Activity saveActivity(Activity activity){ return activityRepository.save(activity);}

    public void deleteActivityByActivityKeyAndProject(String string, String project){
        activityRepository.deleteActivityByActivityKeyAndProject(string, project);
    }
}
