package eu.rationence.pat.service;

import eu.rationence.pat.model.MonthlyNote;
import eu.rationence.pat.repository.MonthlyNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class MonthlyNoteService {
    private final MonthlyNoteRepository monthlyNoteRepository;
    @Autowired
    public MonthlyNoteService(MonthlyNoteRepository monthlyNoteRepository) {
        this.monthlyNoteRepository = monthlyNoteRepository;
    }

    public MonthlyNote find(String username, Date date) {
        return monthlyNoteRepository.getMonthlyNoteByUsernameAndDate(username, date);
    }

    public MonthlyNote save(MonthlyNote MonthlyNote){
        return monthlyNoteRepository.save(MonthlyNote);
    }

    public void delete(String username, Date date){
        monthlyNoteRepository.deleteMonthlyNoteByUsernameAndDate(username, date);
    }
}
