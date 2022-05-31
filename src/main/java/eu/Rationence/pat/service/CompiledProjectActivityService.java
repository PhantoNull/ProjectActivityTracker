package eu.Rationence.pat.service;

import eu.Rationence.pat.model.CompiledProjectActivity;
import eu.Rationence.pat.repository.CompiledProjectActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Configurable
public class CompiledProjectActivityService {
    @Autowired
    private CompiledProjectActivityRepository compiledProjectActivityRepository;

    public CompiledProjectActivity find(String activityKey, String project, String username, String location, Date date){
        return compiledProjectActivityRepository.getCompiledProjectActivityByActivityKeyAndProjectAndUsernameAndLocationNameAndDate(activityKey, project, username, location, date);
    }
    public List<CompiledProjectActivity> findCompiledActivities(String username) {
        return compiledProjectActivityRepository.findCompiledProjectActivitiesByUsername(username);
    }
    public List<CompiledProjectActivity> findCompiledActivities(String username, int month, int year) {
        return compiledProjectActivityRepository.findCompiledProjectActivitiesByUsernameAndMonthAndYear(username, month, year);
    }
    public List<CompiledProjectActivity> findCompiledActivities(String username, String location, String project, String activityKey, int month, int year) {
        return compiledProjectActivityRepository.findCompiledProjectActivitiesListByUsernameAndLocationNameAndProjectAndActivityKeyAndMonthAndYear(username, location, project, activityKey, month, year);
    }
    public CompiledProjectActivity save(CompiledProjectActivity compiledProjectActivity){
        return compiledProjectActivityRepository.save(compiledProjectActivity);
    }
    public void delete(String activityKey, String project, String username, String location, Date date){
        compiledProjectActivityRepository.deleteCompiledProjectActivityByActivityKeyAndProjectAndUsernameAndLocationNameAndDate(activityKey, project, username, location, date);
    }
}
