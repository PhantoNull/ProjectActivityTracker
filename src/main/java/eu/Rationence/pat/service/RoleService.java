package eu.Rationence.pat.service;

import eu.Rationence.pat.repository.RoleRepository;
import eu.Rationence.pat.model.Role;
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

    public Role findRole(String role){
        return roleRepository.getRoleByRoleName(role);
    }

    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    public Role save(Role role){ return roleRepository.save(role);}

    public void deleteRole(String roleName){ roleRepository.deleteRoleByRoleName(roleName);}
}
