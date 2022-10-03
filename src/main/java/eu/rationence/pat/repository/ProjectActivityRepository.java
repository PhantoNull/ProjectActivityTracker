package eu.rationence.pat.repository;

import eu.rationence.pat.model.ProjectActivity;
import eu.rationence.pat.model.composite_keys.ProjectActivityCompositeKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ProjectActivityRepository extends CrudRepository<ProjectActivity, ProjectActivityCompositeKey> {
    ProjectActivity getActivityByActivityKeyAndProject(String activityKey, String projectKey);

    @Query(value = "SELECT * FROM PAT_Activities WHERE c_Project = ?1 ORDER BY d_Start ASC", nativeQuery = true)
    List<ProjectActivity> findActivitiesByProject(String projectKey);

    <S extends ProjectActivity> S save(S projectActivity);

    @Transactional
    void deleteActivityByActivityKeyAndProject(String activityKey, String projectKey);
}


