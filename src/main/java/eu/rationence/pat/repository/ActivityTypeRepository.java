package eu.rationence.pat.repository;

import eu.rationence.pat.model.ActivityType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ActivityTypeRepository extends CrudRepository<ActivityType, String> {
    ActivityType getActivityTypeByActivityTypeKey(String activityTypeKey);

    List<ActivityType> findAll();

    <S extends ActivityType> S save(S activityType);

    @Transactional
    void deleteActivityTypeByActivityTypeKey(String string);
}


