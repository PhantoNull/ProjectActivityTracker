package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.Utente;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UtentiRepository extends CrudRepository<Utente, String> {
    Utente getUtenteByUsername(String string);
    List<Utente> findAll();

}
