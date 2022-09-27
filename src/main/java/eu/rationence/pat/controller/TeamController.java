package eu.rationence.pat.controller;

import eu.rationence.pat.model.Team;
import eu.rationence.pat.model.User;
import eu.rationence.pat.model.dto.TeamDTO;
import eu.rationence.pat.service.TeamService;
import eu.rationence.pat.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class TeamController {
    private static final String CLASS_DESC = "Team";
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final TeamService teamService;

    @Autowired
    public TeamController(ModelMapper modelMapper, UserService userService, TeamService teamService) {
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.teamService = teamService;
    }

    @GetMapping("/teams")
    public String teams(Model model, Principal principal) {
        model.addAttribute("userList", userService.findAll());
        model.addAttribute("teamList", teamService.findAll());
        String username = principal.getName();
        User userRepo = userService.find(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "teams";
    }

    @PostMapping("/teams")
    public ResponseEntity<String> addTeam(@Valid TeamDTO teamDTO,
                                          @RequestParam(value = "teamAdmin") String teamAdminKey,
                                          BindingResult result) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Team team = modelMapper.map(teamDTO, Team.class);
            if (team.getTeamName().length() < 1)
                return AdviceController.responseBadRequest(CLASS_DESC + " name can't be blank");
            else if (team.getTeamDesc().length() < 1)
                return AdviceController.responseBadRequest(CLASS_DESC + " description can't be blank");
            else if (teamService.find(team.getTeamName()) != null)
                return AdviceController.responseConflict(team.getTeamName() + " has been already created");
            User userRepo = userService.find(teamAdminKey);
            if (userRepo == null)
                return AdviceController.responseNotFound(teamAdminKey + " is not a valid user for" + CLASS_DESC + " Manager");
            Pattern p = Pattern.compile("[^a-z0-9._-]", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(team.getTeamName());
            if(m.find())
                return AdviceController.responseBadRequest("Cannot create " + CLASS_DESC + ". Team key should contain only allowed characters: [a-zA-Z0-9._-]");
            teamService.save(team);
            return AdviceController.responseOk(CLASS_DESC + " '" + team.getTeamName() + "' saved");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/teams")
    public ResponseEntity<String> updateTeam(@Valid TeamDTO teamDTO,
                                             @RequestParam(value = "teamAdmin") String teamAdminKey,
                                             BindingResult result) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Team team = modelMapper.map(teamDTO, Team.class);
            User userRepo = userService.find(teamAdminKey);
            if (userRepo == null)
                return AdviceController.responseNotFound(teamAdminKey + " is not a valid user");
            Team teamRepo = teamService.find(team.getTeamName());
            if (teamRepo == null)
                return AdviceController.responseNotFound(team.getTeamName() + " is not a valid " + CLASS_DESC + "Manager");
            teamService.save(team);
            return AdviceController.responseOk(CLASS_DESC + " '" + team.getTeamName() + "' updated");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/teams")
    public ResponseEntity<String> deleteTeam(@Valid TeamDTO teamDTO,
                                             BindingResult result) {
        try {
            if (result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Team team = modelMapper.map(teamDTO, Team.class);
            Team teamRepo = teamService.find(team.getTeamName());
            if (teamRepo == null)
                return AdviceController.responseNotFound(team.getTeamName() + " does not exists");
            teamService.deleteTeam(team.getTeamName());
            return AdviceController.responseOk(CLASS_DESC + " '" + team.getTeamName() + "' successfully deleted");
        } catch (DataIntegrityViolationException e) {
            return AdviceController.responseForbidden("Cannot delete " + CLASS_DESC + " '" + teamDTO.getTeamName() + "' (Constraint violation)");
        } catch (Exception e) {
            return AdviceController.responseServerError(e.getMessage());
        }
    }
}
