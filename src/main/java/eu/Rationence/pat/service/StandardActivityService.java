package eu.Rationence.pat.service;

import eu.Rationence.pat.model.StandardActivity;
import eu.Rationence.pat.repository.StandardActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class StandardActivityService {
    @Autowired
    private StandardActivityRepository stdRepository;

    public StandardActivity findStandardActivityByActivityKey(String activityKey){
        return stdRepository.getStandardActivitiesByActivityKey(activityKey);
    }
    public List<StandardActivity> findAll() { return stdRepository.findAll();}

    public StandardActivity save(StandardActivity standardActivity){ return stdRepository.save(standardActivity);};

    public void delete(String string){
        stdRepository.deleteStandardActivityByActivityKey(string);}
}
