package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.Role;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.RoleService;
import eu.Rationence.pat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


@Controller
public class RoleController {
    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/roles")
    public String roles(Model model, Principal principal) {
        model.addAttribute("listaRuoli", roleService.findAll());
        String username = principal.getName();
        User userRepo = userService.findUser(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "roles";
    }

    @PostMapping("/roles")
    public ResponseEntity<String> addRole(@Valid Role role,
                                          BindingResult result) {
        try {
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            roleService.save(role);
            return AdviceController.responseOk("Role '" + role.getRoleName() + "' saved.");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }

    }

    @PutMapping("/roles")
    public ResponseEntity<String> updateRole(@Valid Role role,
                                          BindingResult result) {
        try {
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            if(roleService.findRole(role.getRoleName()) == null)
                return AdviceController.responseNotFound("Cannot update Role " + role.getRoleName() + "(not found)");
            roleService.save(role);
            return AdviceController.responseOk("Role '" + role.getRoleName() + "' saved.");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }

    }

    @DeleteMapping("/roles")
    public ResponseEntity<String> deleteRole(@Valid Role role,
                                             BindingResult result) {
        try {
            if (role.getRoleName().equals("ADMIN") || role.getRoleName().equals("USER"))
                return AdviceController.responseForbidden("Can't delete default ADMIN or USER role");
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            roleService.deleteRole(role.getRoleName());
            return AdviceController.responseOk("Role '" + role.getRoleName() + "' deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete role '" + role.getRoleName() + "'. (Constraint violation)");
        }
        catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }
}
