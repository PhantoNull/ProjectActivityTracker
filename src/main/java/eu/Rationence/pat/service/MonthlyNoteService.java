package eu.Rationence.pat.service;



import eu.Rationence.pat.model.MonthlyNote;
import eu.Rationence.pat.repository.MonthlyNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Configurable
public class MonthlyNoteService {
    @Autowired
    private MonthlyNoteRepository monthlyNoteRepository;

    public MonthlyNote find(String username, Date date){return monthlyNoteRepository.getMonthlyNoteByUsernameAndDate(username, date);}

    public MonthlyNote save(MonthlyNote MonthlyNote){ return monthlyNoteRepository.save(MonthlyNote);};

    public void delete(String username, Date date){ monthlyNoteRepository.deleteMonthlyNoteByUsernameAndDate(username, date);}
}
