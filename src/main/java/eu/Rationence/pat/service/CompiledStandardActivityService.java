package eu.Rationence.pat.service;

import eu.Rationence.pat.model.CompiledStandardActivity;
import eu.Rationence.pat.repository.CompiledStandardActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Configurable
public class CompiledStandardActivityService {
    @Autowired
    private CompiledStandardActivityRepository compiledStandardActivityRepository;

    public CompiledStandardActivity findActivityByActivityKeyAndStandardAndUsernameAndDate(String activityKey, String project, String username, Date date){
        return compiledStandardActivityRepository.getCompiledStandardActivityByActivityKeyAndUsernameAndDate(activityKey, username, date);
    }
    public List<CompiledStandardActivity> findActivitiesByUsername(String username) {
        return compiledStandardActivityRepository.findCompiledStandardActivitiesByUsername(username);
    }
    public List<CompiledStandardActivity> findActivitiesByUsernameAndMonthAndYear(String username, int month, int year) {
        return compiledStandardActivityRepository.findCompiledStandardActivitiesByUsernameAndMonthAndYear(username, month, year);
    }
    public List<CompiledStandardActivity> findCompiledStandardActivitiesListByUsernameAndLocationNameAndMonthAndYear(String username, String location, int month, int year) {
        return compiledStandardActivityRepository.findCompiledStandardActivitiesListByUsernameAndLocationNameAndMonthAndYear(username, location, month, year);
    }
    public CompiledStandardActivity saveCompiledStandardActivity(CompiledStandardActivity compiledStandardActivity){
        return compiledStandardActivityRepository.save(compiledStandardActivity);
    }
    public void deleteCompiledStandardActivityByActivityKeyAndStandardAndUsernameAndDate(String activityKey, String username, Date date){
        compiledStandardActivityRepository.deleteCompiledStandardActivityByActivityKeyAndUsernameAndDate(activityKey, username, date);
    }
}
