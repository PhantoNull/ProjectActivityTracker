package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.Role;
import eu.Rationence.pat.model.Team;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.RoleService;
import eu.Rationence.pat.service.TeamService;
import eu.Rationence.pat.service.UserService;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mapping.model.MappingInstantiationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
@AllArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final TeamService teamService;
    @Autowired
    private final RoleService roleService;

    @GetMapping ("/users")
    public String utenti(Model model, Principal principal) {
        model.addAttribute("listaUtenti", userService.findAll());
        model.addAttribute("listaTeams", teamService.findAll());
        model.addAttribute("listaRuoli", roleService.findAll());
        model.addAttribute("pageTitle", "PAT Prova");
        String username = principal.getName();
        User userRepo = userService.findUserByUsername(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "users";
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@Valid User user,
                                          @RequestParam(value="team") String teamKey,
                                          @RequestParam(value="role") String roleKey,
                                          @RequestParam(value="cost") String cost,
                                          BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            if(userService.findUserByUsername(user.getUsername()) != null)
                return AdviceController.responseConflict(user.getUsername() + " has been already created");
            ResponseEntity<String> validityError = checkUserValidity(user, teamKey, roleKey, cost);
            if(validityError != null)
                return validityError;
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPasswordHash(encoder.encode("RatioPassTemp!"));
            Team teamRepo = teamService.findTeamByTeamName(teamKey);
            Role roleRepo = roleService.findRoleByRoleName(roleKey);
            user.setTeam(teamRepo);
            user.setRole(roleRepo);
            userService.saveUser(user);
            return ResponseEntity.ok("'" + user.getUsername() + "' created.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/users")
    public ResponseEntity<String> updateUser(@Valid User user,
                                          @RequestParam(value="team") String teamKey,
                                          @RequestParam(value="role") String roleKey,
                                          @RequestParam(value="cost") String cost,
                                          BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            User userRepo = userService.findUserByUsername(user.getUsername());
            if(userRepo == null)
                return AdviceController.responseNotFound("Can't update user " + user.getUsername() + " (User does not exists)");
            ResponseEntity<String> validityError = checkUserValidity(user, teamKey, roleKey, cost);
            if(validityError != null)
                return validityError;
            user.setPasswordHash(userRepo.getPasswordHash());
            Team teamRepo = teamService.findTeamByTeamName(teamKey);
            Role roleRepo = roleService.findRoleByRoleName(roleKey);
            user.setTeam(teamRepo);
            user.setRole(roleRepo);
            userService.saveUser(user);
            return AdviceController.responseOk("'" + user.getUsername() + "' updated.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PostMapping("/resetPasswordUser")
    public ResponseEntity<String> resetPasswordUser(@Valid User user,
                                             @RequestParam(value="team") String teamKey,
                                             @RequestParam(value="role") String roleKey,
                                             @RequestParam(value="cost") String cost,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            User userRepo = userService.findUserByUsername(user.getUsername());
            if(userRepo == null)
                return AdviceController.responseNotFound("Can't reset " + user.getUsername() + "'s password. (User does not exists)");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userRepo.setPasswordHash(encoder.encode("RatioPassTemp!"));
            userService.saveUser(userRepo);
            return AdviceController.responseOk("'" + user.getUsername() + "'s password reset.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PostMapping("/changePasswordUser")
    public ResponseEntity<String> changePasswordUser(@RequestParam(value="password") String newPass,
                                                     Principal principal){
        try{
            String username = principal.getName();
            User userRepo = userService.findUserByUsername(username);
            if(userRepo == null)
                return AdviceController.responseNotFound("Can't reset " + username + "'s password. (User does not exists)");
            Pattern pattern = Pattern.compile("^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,64}$");
            Matcher matcher = pattern.matcher(newPass);
            if(!matcher.find())
                return AdviceController.responseBadRequest("Password does not match minimum requirements (server)");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userRepo.setPasswordHash(encoder.encode(newPass));
            userService.saveUser(userRepo);
            return AdviceController.responseOk("Your password has been successfully changed");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/users")
    public ResponseEntity<String> deleteUser(@Valid User user,
                                                    @RequestParam(value="team") String teamKey,
                                                    @RequestParam(value="role") String roleKey,
                                                    @RequestParam(value="cost") String cost,
                                                    BindingResult result,
                                                    Principal principal){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            User userRepo = userService.findUserByUsername(user.getUsername());
            if(userRepo == null)
                return AdviceController.responseNotFound("Cannot delete '" + user.getUsername() + "' account. (User does not exists)");
            String username = principal.getName();
            if(userRepo.getUsername().equalsIgnoreCase(username))
                return AdviceController.responseForbidden("Cannot delete your account while logged in.");
            userService.deleteUserByUsername(user.getUsername());
            return AdviceController.responseOk("'" + user.getUsername() + "' successfully deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete '" + user.getUsername() + "' account. (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    private boolean isNumericString(String string){
        for (int i=0; i< string.length(); i++){
            if("0123456789".indexOf(string.charAt(i)) == -1)
                return false;
        }
        return true;
    }

    private ResponseEntity<String> checkUserValidity(User user, String teamKey, String roleKey, String cost){
        if(!user.equals(userService.findUserByEmail(user.getEmail())) && userService.findUserByEmail(user.getEmail()) != null)
            return AdviceController.responseConflict(user.getEmail() + " is already used by another user");
        if(!EmailValidator.getInstance().isValid(user.getEmail()))
            return AdviceController.responseBadRequest(user.getUsername() + "'s email '" + user.getEmail() + "' is not valid");
        if(!isNumericString(user.getTime()) || user.getTime().length() != 5)
            return AdviceController.responseBadRequest(user.getUsername() + "'s time '" + user.getTime() + "' is not valid");
        if(!isNumericString(cost))
            return AdviceController.responseBadRequest(user.getUsername() + "'s cost '" + cost + "' is not valid");
        Team teamRepo = teamService.findTeamByTeamName(teamKey);
        Role roleRepo = roleService.findRoleByRoleName(roleKey);
        if(teamRepo == null)
            return AdviceController.responseNotFound("Team" + teamKey + " not found.");
        if(roleRepo == null)
            return AdviceController.responseNotFound("Role" + roleKey + " not found.");
        return null;
    }

}
