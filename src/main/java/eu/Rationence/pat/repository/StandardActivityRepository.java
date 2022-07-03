package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.StandardActivity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface StandardActivityRepository extends CrudRepository<StandardActivity, String> {
    StandardActivity getStandardActivitiesByActivityKey(String activityKey);
    List<StandardActivity> findAll();
    StandardActivity save(StandardActivity std);
    @Transactional
    void deleteStandardActivityByActivityKey(String activityKey);
}


