package eu.rationence.pat.repository;

import eu.rationence.pat.model.MonthlyNote;
import eu.rationence.pat.model.compositekeys.MonthlyNoteCompositeKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface MonthlyNoteRepository extends CrudRepository<MonthlyNote, MonthlyNoteCompositeKey> {
    MonthlyNote getMonthlyNoteByUsernameAndDate(String username, Date date);
    MonthlyNote save(MonthlyNote monthlyNote);
    @Transactional
    void deleteMonthlyNoteByUsernameAndDate(String username, Date date);
}


