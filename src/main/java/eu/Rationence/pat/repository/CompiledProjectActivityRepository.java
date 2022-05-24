package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.CompiledProjectActivity;
import eu.Rationence.pat.model.CompiledProjectActivityCompositeKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface CompiledProjectActivityRepository extends CrudRepository<CompiledProjectActivity, CompiledProjectActivityCompositeKey> {
    CompiledProjectActivity getCompiledProjectActivityByActivityKeyAndProjectAndUsernameAndLocationNameAndDate(String activityKey, String projectKey, String username, String location, Date date);

    List<CompiledProjectActivity> findCompiledProjectActivitiesByUsername(String username);

    @Query(value = "SELECT * FROM PAT_CompiledProjectActivities WHERE c_Username = ?1 and DATEPART(month, d_Date) = ?2 and DATEPART(year, d_Date) = ?3", nativeQuery = true)
    List<CompiledProjectActivity> findCompiledProjectActivitiesByUsernameAndMonthAndYear(String username, int month, int year);

    @Query(value = "SELECT * FROM PAT_CompiledProjectActivities WHERE c_Username = ?1 and c_Location = ?2 and DATEPART(month, d_Date) = ?3 and DATEPART(year, d_Date) = ?4" , nativeQuery = true)
    List<CompiledProjectActivity> findCompiledProjectActivitiesListByUsernameAndLocationNameAndMonthAndYear(String username, String location, int month, int year);

    CompiledProjectActivity save(CompiledProjectActivity compiledProjectActivity);

    @Transactional
    void deleteCompiledProjectActivityByActivityKeyAndProjectAndUsernameAndDate(String activityKey, String projectKey, String username, Date date);
}


