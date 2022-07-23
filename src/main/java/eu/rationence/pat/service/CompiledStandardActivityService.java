package eu.rationence.pat.service;

import eu.rationence.pat.model.CompiledStandardActivity;
import eu.rationence.pat.repository.CompiledStandardActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CompiledStandardActivityService {
    private final CompiledStandardActivityRepository compiledStandardActivityRepository;
    @Autowired
    public CompiledStandardActivityService(CompiledStandardActivityRepository compiledStandardActivityRepository) {
        this.compiledStandardActivityRepository = compiledStandardActivityRepository;
    }

    public CompiledStandardActivity find(String activityKey, String username, Date date){
        return compiledStandardActivityRepository.getCompiledStandardActivityByActivityKeyAndUsernameAndDate(activityKey, username, date);
    }
    public List<CompiledStandardActivity> find(int month, int year) {
        return compiledStandardActivityRepository.findCompiledStandardActivitiesByMonthAndYear(month, year);
    }
    public List<CompiledStandardActivity> find(String username, int month, int year) {
        return compiledStandardActivityRepository.findCompiledStandardActivitiesByUsernameAndMonthAndYear(username, month, year);
    }
    public List<CompiledStandardActivity> find(String username, String location, String activityKey, int month, int year) {
        return compiledStandardActivityRepository.findCompiledStandardActivitiesListByUsernameAndLocationNameAndActivityKeyAndMonthAndYear(username, location, activityKey, month, year);
    }
    public CompiledStandardActivity save(CompiledStandardActivity compiledStandardActivity){
        return compiledStandardActivityRepository.save(compiledStandardActivity);
    }
    public void delete(String activityKey, String username, String location, Date date){
        compiledStandardActivityRepository.deleteCompiledStandardActivityByActivityKeyAndUsernameAndLocationNameAndDate(activityKey, username, location, date);
    }
}
