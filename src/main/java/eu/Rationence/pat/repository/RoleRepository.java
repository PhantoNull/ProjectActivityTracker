package eu.Rationence.pat.repository;

import eu.Rationence.pat.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface RoleRepository extends CrudRepository<Role, String> {
    Role getRoleByRoleName(String roleName);
    List<Role> findAll();
    Role save(Role role);
    @Transactional
    void deleteRoleByRoleName(String roleName);
}


