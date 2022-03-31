package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.Role;
import eu.Rationence.pat.model.Team;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.RoleService;
import eu.Rationence.pat.service.TeamService;
import eu.Rationence.pat.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
    public ResponseEntity<String> addUser(@Valid User user, @RequestParam(value="team") String teamKey , @RequestParam(value="role") String roleKey){
        try{
            if(user.equals(userService.findUtenteByUsername(user.getUsername())))
                return ResponseEntity.status(409).body("ERROR: " + user.getUsername() + "is in the database");
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
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void updateUser(@Valid User user, @RequestParam(value="team") String teamKey , @RequestParam(value="role") String roleKey){
        Team teamRepo = teamService.findTeamByTeamName(teamKey);
        Role roleRepo = roleService.findRoleByRoleName(roleKey);
        User userRepo = userService.findUtenteByUsername(user.getUsername());
        user.setPasswordHash(userRepo.getPasswordHash());
        user.setTeam(teamRepo);
        user.setRole(roleRepo);
        userService.saveUser(user);
    }

    @PostMapping("/resetPasswordUser")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void resetPasswordUser(@Valid User user, @RequestParam(value="team") String teamKey , @RequestParam(value="role") String roleKey){
        User userRepo = userService.findUtenteByUsername(user.getUsername());
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userRepo.setPasswordHash(encoder.encode("RatioPassTemp!"));
        userRepo.setPasswordHash(userRepo.getPasswordHash());
        userService.saveUser(userRepo);
    }

    @PostMapping("/deleteUser")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void deleteUser(@Valid User user){
        User userRepo = userService.findUtenteByUsername(user.getUsername());
        userService.deleteUserByUsername(userRepo.getUsername());
    }


}
