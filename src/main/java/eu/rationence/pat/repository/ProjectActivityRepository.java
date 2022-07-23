package eu.rationence.pat.repository;

import eu.rationence.pat.model.composite_keys.ProjectActivityCompositeKey;
import eu.rationence.pat.model.ProjectActivity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProjectActivityRepository extends CrudRepository<ProjectActivity, ProjectActivityCompositeKey> {
    ProjectActivity getActivityByActivityKeyAndProject(String activityKey, String projectKey);
    List<ProjectActivity> findActivitiesByProject(String projectKey);
    ProjectActivity save(ProjectActivity projectActivity);
    @Transactional
    void deleteActivityByActivityKeyAndProject(String activityKey, String projectKey);
}


