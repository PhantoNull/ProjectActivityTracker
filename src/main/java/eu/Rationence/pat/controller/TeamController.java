package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.Team;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.TeamService;
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
public class TeamController {
    private final UserService userService;
    private final TeamService teamService;

    @Autowired
    public TeamController(UserService userService, TeamService teamService) {
        this.userService = userService;
        this.teamService = teamService;
    }

    @GetMapping ("/teams")
    public String teams(Model model, Principal principal) {
        model.addAttribute("userList", userService.findAll());
        model.addAttribute("teamList", teamService.findAll());
        String username = principal.getName();
        User userRepo = userService.findUser(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "teams";
    }

    @PostMapping("/teams")
    public ResponseEntity<String> addTeam(@Valid Team team,
                                          @RequestParam(value="teamAdmin") String teamAdminKey,
                                          BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            else if(team.getTeamName().length() < 1)
                return AdviceController.responseBadRequest("Team Name can't be blank");
            else if(team.getTeamDesc().length() < 1)
                return AdviceController.responseBadRequest("Description can't be blank");
            else if(teamService.findTeamByTeamName(team.getTeamName()) != null)
                return AdviceController.responseConflict(team.getTeamName() + " has been already created");
            User userRepo = userService.findUser(teamAdminKey);
            if(userRepo == null)
                return AdviceController.responseNotFound(teamAdminKey + " is not a valid user. (not found)");
            teamService.saveTeam(team);
            return AdviceController.responseOk("Team '" + team.getTeamName() + "' saved.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/teams")
    public ResponseEntity<String> updateTeam(@Valid Team team,
                                             @RequestParam(value="teamAdmin") String teamAdminKey,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            User userRepo = userService.findUser(teamAdminKey);
            if(userRepo == null)
                return AdviceController.responseNotFound(teamAdminKey + " is not a valid user. (not found)");
            Team teamRepo = teamService.findTeamByTeamName(team.getTeamName());
            if(teamRepo == null)
                return AdviceController.responseNotFound(team.getTeamName() + " is not a valid team. (not found)");
            teamService.saveTeam(team);
            return AdviceController.responseOk("Team '" + team.getTeamName() + "' updated.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/teams")
    public ResponseEntity<String> deleteTeam(@Valid Team team,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Team teamRepo = teamService.findTeamByTeamName(team.getTeamName());
            if(teamRepo == null)
                return AdviceController.responseNotFound(team.getTeamName() + " does not exists");
            teamService.deleteTeam(team.getTeamName());
            return AdviceController.responseOk("Team '" + team.getTeamName() + "' successfully deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete team '" + team.getTeamName() + "'. (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }
}
