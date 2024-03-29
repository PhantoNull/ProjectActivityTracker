package eu.rationence.pat.repository;

import eu.rationence.pat.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    User getUserByUsername(String username);

    User getUserByEmail(String email);

    List<User> findAll();

    <S extends User> S save(S user);

    @Transactional
    void deleteUserByUsername(String username);
}


