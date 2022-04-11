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

    public StandardActivity findStandardActivityByActivityKey(String string){
        return stdRepository.getStandardActivitiesByActivityKey(string);
    }
    public List<StandardActivity> findAll() { return stdRepository.findAll();}

    public StandardActivity saveStdActivity(StandardActivity stdAct){ return stdRepository.save(stdAct);};

    public void deleteStandardActivityByStandardActivityKey(String string){
        stdRepository.deleteStandardActivityByActivityKey(string);}
}
