package eu.Rationence.pat.service;

import eu.Rationence.pat.model.UserActivity;
import eu.Rationence.pat.repository.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class UserActivityService {
    @Autowired
    private UserActivityRepository userActivityRepository;

    public UserActivity findUserActivityByActivityKeyAndProjectAndUsername(String string, String project, String username){
        return userActivityRepository.getUserActivityByActivityKeyAndProjectAndUsername(string, project, username);
    }
    public List<UserActivity> findUserActivitiesByProjectAndActivityKey(String project, String activityKey) {
        return userActivityRepository.findUserActivitiesByProjectAndActivityKey(project, activityKey);
    }

    public UserActivity saveUserActivity(UserActivity userActivity){ return userActivityRepository.save(userActivity);}

    public void deleteUserActivityByActivityKeyAndProjectAndUsername(String string, String project, String username){
        userActivityRepository.deleteUserActivityByActivityKeyAndProjectAndUsername(string, project, username);
    }
}