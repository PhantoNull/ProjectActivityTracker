package eu.rationence.pat.controller;

import eu.rationence.pat.model.Role;
import eu.rationence.pat.model.User;
import eu.rationence.pat.service.RoleService;
import eu.rationence.pat.service.UserService;
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
    private static final String CLASS_DESC = "Role";

    @Autowired
    public RoleController(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @GetMapping("/roles")
    public String roles(Model model, Principal principal) {
        model.addAttribute("listaRuoli", roleService.findAll());
        String username = principal.getName();
        User userRepo = userService.find(username);
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
            return AdviceController.responseOk(CLASS_DESC + " '" + role.getRoleName() + "' saved");
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
            if(roleService.find(role.getRoleName()) == null)
                return AdviceController.responseNotFound("Cannot update " + CLASS_DESC +  " " + role.getRoleName());
            roleService.save(role);
            return AdviceController.responseOk(CLASS_DESC + " '" + role.getRoleName() + "' saved");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }

    }

    @DeleteMapping("/roles")
    public ResponseEntity<String> deleteRole(@Valid Role role,
                                             BindingResult result) {
        try {
            if (role.getRoleName().equals("ADMIN") || role.getRoleName().equals("USER"))
                return AdviceController.responseForbidden("Can't delete default ADMIN or USER " + CLASS_DESC);
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            roleService.delete(role.getRoleName());
            return AdviceController.responseOk(CLASS_DESC + " '" + role.getRoleName() + "' deleted");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete " + CLASS_DESC + " '" + role.getRoleName() + "' (Constraint violation)");
        }
        catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }
}
