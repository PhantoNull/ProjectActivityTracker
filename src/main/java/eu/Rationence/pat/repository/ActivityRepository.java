package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.Activity;
import eu.Rationence.pat.model.ActivityCompositeKey;
import eu.Rationence.pat.model.Project;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, ActivityCompositeKey> {
    Activity getActivityByActivityKeyAndProject(String string, Project project);
    List<Activity> findActivitiesByProject(Project project);
    Activity save(Activity activity);
    @Transactional
    void deleteActivityByActivityKeyAndProject(String string, Project project);
}


