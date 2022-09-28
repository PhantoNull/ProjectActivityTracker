package eu.rationence.pat.repository;

import eu.rationence.pat.model.StandardActivity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface StandardActivityRepository extends CrudRepository<StandardActivity, String> {
    StandardActivity getStandardActivitiesByActivityKey(String activityKey);

    List<StandardActivity> findAll();

    <S extends StandardActivity> S save(S std);

    @Transactional
    void deleteStandardActivityByActivityKey(String activityKey);
}


