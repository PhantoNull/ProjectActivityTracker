package eu.rationence.pat.service;

import eu.rationence.pat.model.Role;
import eu.rationence.pat.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role find(String role){
        return roleRepository.getRoleByRoleName(role);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role save(Role role){ return roleRepository.save(role);}

    public void delete(String roleName){ roleRepository.deleteRoleByRoleName(roleName);}
}
