package eu.rationence.pat.repository;

import eu.rationence.pat.model.composite_keys.UserActivityCompositeKey;
import eu.rationence.pat.model.UserActivity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserActivityRepository extends CrudRepository<UserActivity, UserActivityCompositeKey> {
    UserActivity getUserActivityByActivityKeyAndProjectAndUsername(String activityKey, String projectKey, String username);
    List<UserActivity> findUserActivitiesByProjectAndActivityKey(String projectKey, String activityKey);
    UserActivity save(UserActivity userActivity);
    @Transactional
    void deleteUserActivityByActivityKeyAndProjectAndUsername(String activityKey, String projectKey, String username);
}


