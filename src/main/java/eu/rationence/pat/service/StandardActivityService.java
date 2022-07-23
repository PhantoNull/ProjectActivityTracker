package eu.rationence.pat.service;

import eu.rationence.pat.model.StandardActivity;
import eu.rationence.pat.repository.StandardActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StandardActivityService {
    private final StandardActivityRepository stdRepository;
    @Autowired
    public StandardActivityService(StandardActivityRepository stdRepository) {
        this.stdRepository = stdRepository;
    }

    public StandardActivity findStandardActivityByActivityKey(String activityKey){
        return stdRepository.getStandardActivitiesByActivityKey(activityKey);
    }
    public List<StandardActivity> findAll() { return stdRepository.findAll();}

    public StandardActivity save(StandardActivity standardActivity){ return stdRepository.save(standardActivity);}

    public void delete(String string){
        stdRepository.deleteStandardActivityByActivityKey(string);}
}
