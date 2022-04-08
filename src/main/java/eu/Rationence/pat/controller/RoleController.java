package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.Role;
import eu.Rationence.pat.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@AllArgsConstructor
public class RoleController {
    @Autowired
    private final RoleService roleService;

    @GetMapping("/roles")
    public String roles(Model model) {
        model.addAttribute("listaRuoli", roleService.findAll());
        return "roles";
    }

    @PostMapping("/addRole")
    public ResponseEntity<String> addRole(@Valid Role role) {
        try {
            roleService.saveRole(role);
            return ResponseEntity.ok("Role '" + role.getRoleName() + "' saved.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }

    }

    @PostMapping("/deleteRole")
    public ResponseEntity<String> deleteRole(@Valid Role role) {
        if (role.getRoleName().equals("ADMIN") || role.getRoleName().equals("USER"))
            return ResponseEntity.badRequest()
                    .body("ERROR : Can't delete default ADMIN or USER role");
        try {
            roleService.deleteRoleByRoleName(role.getRoleName());
            return ResponseEntity.ok("Role '" + role.getRoleName() + "' deleted.");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("ERROR : " + e.getMessage());
        }
    }

}
