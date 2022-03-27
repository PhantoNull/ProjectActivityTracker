package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, String> {
    Role getRoleByRoleName(String string);
    List<Role> findAll();
    Role save(Role role);
    
}


