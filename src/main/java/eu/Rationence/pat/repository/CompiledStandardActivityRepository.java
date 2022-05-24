package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.CompiledProjectActivity;
import eu.Rationence.pat.model.CompiledStandardActivity;
import eu.Rationence.pat.model.CompiledStandardActivityCompositeKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface CompiledStandardActivityRepository extends CrudRepository<CompiledStandardActivity, CompiledStandardActivityCompositeKey> {
    CompiledStandardActivity getCompiledStandardActivityByActivityKeyAndUsernameAndDate(String activityKey, String username, Date date);

    List<CompiledStandardActivity> findCompiledStandardActivitiesByUsername(String username);

    @Query(value = "SELECT * FROM PAT_CompiledStandardActivities WHERE c_Username = ?1 and DATEPART(month, d_Date) = ?2 and DATEPART(year, d_Date) = ?3", nativeQuery = true)
    List<CompiledStandardActivity> findCompiledStandardActivitiesByUsernameAndMonthAndYear(String username, int month, int year);

    @Query(value = "SELECT * FROM PAT_CompiledStandardActivities WHERE c_Username = ?1 AND c_Location = ?2 AND c_Activity = ?3 AND DATEPART(month, d_Date) = ?4 AND DATEPART(year, d_Date) = ?5" , nativeQuery = true)
    List<CompiledStandardActivity> findCompiledStandardActivitiesListByUsernameAndLocationNameAndActivityKeyAndMonthAndYear(String username, String location, String activityKey, int month, int year);

    CompiledStandardActivity save(CompiledStandardActivity compiledStandardActivity);

    @Transactional
    void deleteCompiledStandardActivityByActivityKeyAndUsernameAndDate(String activityKey, String username, Date date);
}


