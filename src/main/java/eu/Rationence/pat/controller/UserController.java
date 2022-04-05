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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;


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
    public String utenti(Model model) {
        model.addAttribute("listaUtenti", userService.findAll());
        model.addAttribute("listaTeams", teamService.findAll());
        model.addAttribute("listaRuoli", roleService.findAll());
        model.addAttribute("pageTitle", "PAT Prova");
        return "users";
    }

    @PostMapping("/addUser")
    public ResponseEntity<String> addUser(@Valid User user,
                                          @RequestParam(value="team") String teamKey,
                                          @RequestParam(value="role") String roleKey,
                                          @RequestParam(value="cost") String cost,
                                          BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body("ERROR: " + result.getAllErrors());
            if(userService.findUserByUsername(user.getUsername()) != null)
                return ResponseEntity.status(409).body("ERROR: " + user.getUsername() + " has been already created");
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
            return ResponseEntity.ok("User '" + user.getUsername() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/updateUser")
    public ResponseEntity<String> updateUser(@Valid User user,
                                          @RequestParam(value="team") String teamKey,
                                          @RequestParam(value="role") String roleKey,
                                          @RequestParam(value="cost") String cost,
                                          BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body("ERROR: " + result.getAllErrors());
            User userRepo = userService.findUserByUsername(user.getUsername());
            if(userRepo == null)
                return ResponseEntity.status(409).body("ERROR: Can't update user " + user.getUsername() + " (User does not exists)");
            ResponseEntity<String> validityError = checkUserValidity(user, teamKey, roleKey, cost);
            if(validityError != null)
                return validityError;
            user.setPasswordHash(userRepo.getPasswordHash());
            Team teamRepo = teamService.findTeamByTeamName(teamKey);
            Role roleRepo = roleService.findRoleByRoleName(roleKey);
            user.setTeam(teamRepo);
            user.setRole(roleRepo);
            userService.saveUser(user);
            return ResponseEntity.ok("User '" + user.getUsername() + "' updated.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
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
                return ResponseEntity.badRequest().body("ERROR: " + result.getAllErrors());
            User userRepo = userService.findUserByUsername(user.getUsername());
            if(userRepo == null)
                return ResponseEntity.status(409).body("ERROR: Can't reset " + user.getUsername() + "'s password. (User does not exists)");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userRepo.setPasswordHash(encoder.encode("RatioPassTemp!"));
            userService.saveUser(userRepo);
            return ResponseEntity.ok("User '" + user.getUsername() + "'s password reset.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/changePasswordUser")
    public ResponseEntity<String> changePasswordUser(@RequestParam(value="password") String newPass,
                                                     Principal principal){
        try{
            String username = principal.getName();
            User userRepo = userService.findUserByUsername(username);
            if(userRepo == null)
                return ResponseEntity.status(409).body("ERROR: Can't reset " + username + "'s password. (User does not exists)");
            if(newPass.length() < 8)
                return ResponseEntity.status(400).body("ERROR: Password length must be at least 8 characters");
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userRepo.setPasswordHash(encoder.encode(newPass));
            userService.saveUser(userRepo);
            return ResponseEntity.ok("Your password has been successfully changed");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/deleteUser")
    public ResponseEntity<String> deleteUser(@Valid User user,
                                                    @RequestParam(value="team") String teamKey,
                                                    @RequestParam(value="role") String roleKey,
                                                    @RequestParam(value="cost") String cost,
                                                    BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body("ERROR: " + result.getAllErrors());
            User userRepo = userService.findUserByUsername(user.getUsername());
            if(userRepo == null)
                return ResponseEntity.status(409).body("ERROR: Cannot delete '" + user.getUsername() + "' account. (User does not exists)");
            userService.deleteUserByUsername(user.getUsername());
            return ResponseEntity.ok("User '" + user.getUsername() + "' succesfully deleted.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("ERROR: Empty input or mismatched input type");
    }

    public boolean isNumericString(String string){
        for (int i=0; i< string.length(); i++){
            if("0123456789".indexOf(string.charAt(i)) == -1)
                return true;
        }
        return false;
    }

    public ResponseEntity<String> checkUserValidity(User user, String teamKey, String roleKey, String cost){
        if(!user.equals(userService.findUserByEmail(user.getEmail())) && userService.findUserByEmail(user.getEmail()) != null)
            return ResponseEntity.status(409).body("ERROR: " + user.getEmail() + " is already used by another user");
        if(!EmailValidator.getInstance().isValid(user.getEmail()))
            return ResponseEntity.badRequest().body("ERROR: " + user.getUsername() + "'s email '" + user.getEmail() + "' is not valid");
        if(isNumericString(user.getTime()) || user.getTime().length() != 5)
            return ResponseEntity.badRequest().body("ERROR: " + user.getUsername() + "'s time '" + user.getTime() + "' is not valid");
        if(isNumericString(cost))
            return ResponseEntity.badRequest().body("ERROR: " + user.getUsername() + "'s cost '" + cost + "' is not valid");
        Team teamRepo = teamService.findTeamByTeamName(teamKey);
        Role roleRepo = roleService.findRoleByRoleName(roleKey);
        if(teamRepo == null)
            return ResponseEntity.badRequest().body("ERROR: Team" + teamKey + " not found.");
        if(roleRepo == null)
            return ResponseEntity.badRequest().body("ERROR: Role" + roleKey + " not found.");
        return null;
    }
}
