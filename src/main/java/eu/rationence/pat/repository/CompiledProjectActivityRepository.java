package eu.rationence.pat.repository;

import eu.rationence.pat.model.CompiledProjectActivity;
import eu.rationence.pat.model.composite_keys.CompiledProjectActivityCompositeKey;
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

    @Query(value = "SELECT * FROM PAT_CompiledProjectActivities WHERE DATEPART(month, d_Date) = ?1 and DATEPART(year, d_Date) = ?2", nativeQuery = true)
    List<CompiledProjectActivity> findCompiledProjectActivitiesByMonthAndYear(int month, int year);

    @Query(value = "SELECT * FROM PAT_CompiledProjectActivities WHERE c_Username = ?1 and DATEPART(month, d_Date) = ?2 and DATEPART(year, d_Date) = ?3", nativeQuery = true)
    List<CompiledProjectActivity> findCompiledProjectActivitiesByUsernameAndMonthAndYear(String username, int month, int year);

    @Query(value = "SELECT * FROM PAT_CompiledProjectActivities WHERE c_Username = ?1 and c_Location = ?2 and c_Project = ?3 and c_Activity = ?4 and DATEPART(month, d_Date) = ?5 and DATEPART(year, d_Date) = ?6", nativeQuery = true)
    List<CompiledProjectActivity> findCompiledProjectActivitiesListByUsernameAndLocationNameAndProjectAndActivityKeyAndMonthAndYear(String username, String location, String projectKey, String activityKey, int month, int year);

    @Query(value= "SELECT COALESCE(SUM(n_Hours),0) FROM PAT_CompiledProjectActivities WHERE c_Project = ?2 and c_Activity = ?1", nativeQuery = true)
    int sumCompiledProjectActivityByActivityKeyAndProjectKey(String activityKey, String projectKey);

    <S extends CompiledProjectActivity> S save(S compiledProjectActivity);

    @Transactional
    void deleteCompiledProjectActivityByActivityKeyAndProjectAndUsernameAndLocationNameAndDate(String activityKey, String projectKey, String username, String location, Date date);
}


