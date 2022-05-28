package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.MonthlyNote;
import eu.Rationence.pat.model.MonthlyNoteCompositeKey;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.Date;

@Repository
public interface MonthlyNoteRepository extends CrudRepository<MonthlyNote, MonthlyNoteCompositeKey> {
    MonthlyNote getMonthlyNoteByUsernameAndDate(String username, Date date);
    MonthlyNote save(MonthlyNote MonthlyNote);
    @Transactional
    void deleteMonthlyNoteByUsernameAndDate(String username, Date date);
}


