package eu.Rationence.pat.service;

import eu.Rationence.pat.model.Role;
import eu.Rationence.pat.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Configurable
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    public Role findRoleByUsername(String string){
        return roleRepository.getRoleByRole(string);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role saveRole(Role role){ return roleRepository.save(role);};
}
