package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.Team;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.TeamService;
import eu.Rationence.pat.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;


@Controller
@AllArgsConstructor
public class TeamController {
    private static final String ERROR_STR = "ERROR: ";

    @Autowired
    private final UserService userService;
    @Autowired
    private final TeamService teamService;

    @GetMapping ("/teams")
    public String teams(Model model, Principal principal) {
        model.addAttribute("userList", userService.findAll());
        model.addAttribute("teamList", teamService.findAll());
        String username = principal.getName();
        User userRepo = userService.findUserByUsername(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "teams";
    }

    @PostMapping("/teams")
    public ResponseEntity<String> addTeam(@Valid Team team,
                                          @RequestParam(value="teamAdmin") String teamAdminKey,
                                          BindingResult result){
        try{
            System.out.println("____________________");
            System.out.println(team);
            System.out.println("____________________");
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            else if(team.getTeamName().length() < 1)
                return ResponseEntity.badRequest().body(ERROR_STR + "Team Name can't be blank");
            else if(team.getTeamDesc().length() < 1)
                return ResponseEntity.badRequest().body(ERROR_STR + "Description can't be blank");
            else if(teamService.findTeamByTeamName(team.getTeamName()) != null)
                return ResponseEntity.status(409).body(ERROR_STR + team.getTeamName() + " has been already created");
            User userRepo = userService.findUserByUsername(teamAdminKey);
            if(userRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + teamAdminKey + " is not a valid user. (not found)");
            teamService.saveTeam(team);
            return ResponseEntity.ok("Team '" + team.getTeamName() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @PutMapping("/teams")
    public ResponseEntity<String> updateTeam(@Valid Team team,
                                             @RequestParam(value="teamAdmin") String teamAdminKey,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            User userRepo = userService.findUserByUsername(teamAdminKey);
            if(userRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + teamAdminKey + " is not a valid user. (not found)");
            Team teamRepo = teamService.findTeamByTeamName(team.getTeamName());
            if(teamRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + team.getTeamName() + " does not exists");
            teamService.saveTeam(team);
            return ResponseEntity.ok("Team '" + team.getTeamName() + "' updated.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @DeleteMapping("/teams")
    public ResponseEntity<String> deleteTeam(@Valid Team team,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            Team teamRepo = teamService.findTeamByTeamName(team.getTeamName());
            if(teamRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + team.getTeamName() + " does not exists");
            teamService.deleteTeamByTeamName(team.getTeamName());
            return ResponseEntity.ok("Team '" + team.getTeamName() + "' successfully deleted.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ERROR_STR + "Empty input or mismatched input type");
    }
}
