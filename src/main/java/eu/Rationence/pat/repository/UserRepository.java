package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User getUtenteByUsername(String string);
    List<User> findAll();
    User save(User user);

}


