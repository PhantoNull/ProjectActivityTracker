package eu.rationence.pat.controller;

import eu.rationence.pat.model.Role;
import eu.rationence.pat.model.User;
import eu.rationence.pat.model.dto.RoleDTO;
import eu.rationence.pat.service.RoleService;
import eu.rationence.pat.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.validation.Valid;
import java.security.Principal;


@Controller
public class RoleController {
    private static final String CLASS_DESC = "Role";
    private final ModelMapper modelMapper;
    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public RoleController(ModelMapper modelMapper, RoleService roleService, UserService userService) {
        this.modelMapper = modelMapper;
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
    public ResponseEntity<String> addRole(@Valid RoleDTO roleDTO,
                                          BindingResult result) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Role role = modelMapper.map(roleDTO, Role.class);
            if (roleService.find(role.getRoleName()) != null) {
                return AdviceController.responseConflict(role.getRoleName() + "has already been created");
            }
            roleService.save(role);
            return AdviceController.responseOk(CLASS_DESC + " '" + role.getRoleName() + "' saved");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }

    }

    @PutMapping("/roles")
    public ResponseEntity<String> updateRole(@Valid RoleDTO roleDTO,
                                             BindingResult result) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Role role = modelMapper.map(roleDTO, Role.class);
            if (roleService.find(role.getRoleName()) == null)
                return AdviceController.responseNotFound("Cannot update " + CLASS_DESC + " " + role.getRoleName());
            roleService.save(role);
            return AdviceController.responseOk(CLASS_DESC + " '" + role.getRoleName() + "' saved");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }

    }

    @DeleteMapping("/roles")
    public ResponseEntity<String> deleteRole(@Valid RoleDTO roleDTO,
                                             BindingResult result) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Role role = modelMapper.map(roleDTO, Role.class);
            if (role.getRoleName().equals("ADMIN") || role.getRoleName().equals("USER"))
                return AdviceController.responseForbidden("Can't delete default ADMIN or USER " + CLASS_DESC);
            else if(roleService.find(role.getRoleName()) == null)
                return AdviceController.responseNotFound("Can't delete role " + role.getRoleName() + " (not found)");
            roleService.delete(role.getRoleName());
            return AdviceController.responseOk(CLASS_DESC + " '" + role.getRoleName() + "' deleted");
        } catch (DataIntegrityViolationException e) {
            return AdviceController.responseForbidden("Cannot delete " + CLASS_DESC + " '" + roleDTO.getRoleName() + "' (Constraint violation)");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }
}
