package eu.Rationence.pat.service;

import eu.Rationence.pat.model.UserActivity;
import eu.Rationence.pat.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActivityService {
    private final UserActivityRepository userActivityRepository;
    @Autowired
    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public UserActivity findUserActivity(String activityKey, String project, String username){
        return userActivityRepository.getUserActivityByActivityKeyAndProjectAndUsername(activityKey, project, username);
    }
    public List<UserActivity> findUserActivities(String project, String activityKey) {
        return userActivityRepository.findUserActivitiesByProjectAndActivityKey(project, activityKey);
    }

    public UserActivity save(UserActivity userActivity){ return userActivityRepository.save(userActivity);}

    public void delete(String string, String project, String username){
        userActivityRepository.deleteUserActivityByActivityKeyAndProjectAndUsername(string, project, username);
    }
}
