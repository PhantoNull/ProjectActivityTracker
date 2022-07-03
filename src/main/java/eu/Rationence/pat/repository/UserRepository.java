package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User getUserByUsername(String username);
    User getUserByEmail(String email);
    List<User> findAll();
    User save(User user);
    @Transactional
    void deleteUserByUsername(String username);
}


