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
            if(user.equals(userService.findUserByUsername(user.getUsername())))
                return ResponseEntity.status(409).body("ERROR: " + user.getUsername() + " is already created");
            if(user.equals(userService.findUserByEmail(user.getEmail())))
                return ResponseEntity.status(409).body("ERROR: " + user.getEmail() + " is already used by another user");
            if(!EmailValidator.getInstance().isValid(user.getEmail()))
                return ResponseEntity.badRequest().body("ERROR: " + user.getUsername() + "'s email '" + user.getEmail() + "' is not valid");
            if(!isNumericString(user.getTime()) || user.getTime().length() != 5)
                return ResponseEntity.badRequest().body("ERROR: " + user.getUsername() + "'s time '" + user.getTime() + "' is not valid");
            if(!isNumericString(cost))
                return ResponseEntity.badRequest().body("ERROR: " + user.getUsername() + "'s cost '" + cost + "' is not valid");
            Team teamRepo = teamService.findTeamByTeamName(teamKey);
            Role roleRepo = roleService.findRoleByRoleName(roleKey);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPasswordHash(encoder.encode("RatioPassTemp!"));
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
    public ResponseEntity<String> updateUser(@Valid User user, @RequestParam(value="team") String teamKey , @RequestParam(value="role") String roleKey){
        try{
            Team teamRepo = teamService.findTeamByTeamName(teamKey);
            Role roleRepo = roleService.findRoleByRoleName(roleKey);
            User userRepo = userService.findUserByUsername(user.getUsername());
            user.setPasswordHash(userRepo.getPasswordHash());
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
    public void resetPasswordUser(@Valid User user, @RequestParam(value="team") String teamKey , @RequestParam(value="role") String roleKey){
        User userRepo = userService.findUserByUsername(user.getUsername());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userRepo.setPasswordHash(encoder.encode("RatioPassTemp!"));
        userRepo.setPasswordHash(userRepo.getPasswordHash());
        userService.saveUser(userRepo);
    }

    @PostMapping("/deleteUser")
    public void deleteUser(@Valid User user){
        User userRepo = userService.findUserByUsername(user.getUsername());
        userService.deleteUserByUsername(userRepo.getUsername());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequestException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("ERROR: Empty input or mismatched input type");
    }

    public boolean isNumericString(String string){
        for (int i=0; i< string.length(); i++){
            if("0123456789".indexOf(string.charAt(i)) == -1)
                return false;
        }
        return true;
    }
}
