package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.*;
import eu.Rationence.pat.service.*;
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
public class ProjectController {
    private final UserService userService;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final ProjectTypeService projectTypeService;
    private final ClientService clientService;
    private final ProjectActivityService projectActivityService;

    @Autowired
    public ProjectController(UserService userService, TeamService teamService, ProjectService projectService, ProjectTypeService projectTypeService, ClientService clientService, ProjectActivityService projectActivityService) {
        this.userService = userService;
        this.teamService = teamService;
        this.projectService = projectService;
        this.projectTypeService = projectTypeService;
        this.clientService = clientService;
        this.projectActivityService = projectActivityService;
    }


    @GetMapping ("/projects")
    public String projects(Model model, Principal principal) {
        model.addAttribute("userList", userService.findAll());
        model.addAttribute("teamList", teamService.findAll());
        model.addAttribute("projectList", projectService.findAll());
        model.addAttribute("projectTypeList", projectTypeService.findAll());
        model.addAttribute("clientList", clientService.findAll());
        for (Project project:projectService.findAll()) {
            int activitiesNumber = projectActivityService.findActivitiesByProject(project.getProjectKey()).size();
            model.addAttribute(project.getProjectKey()+"Activities",activitiesNumber);
        }
        String username = principal.getName();
        User userRepo = userService.findUser(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "projects";
    }

    @PostMapping("/projects")
    public ResponseEntity<String> addProject(@Valid Project project,
                                             @RequestParam(value="team") String teamKey ,
                                             @RequestParam(value="projectManager") String projectManagerKey,
                                             @RequestParam(value="client") String clientKey,
                                             @RequestParam(value="projectType") String projectTypeKey,
                                             @RequestParam(value="value") String value,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            if(projectService.find(project.getProjectKey()) != null)
                return AdviceController.responseConflict(project.getProjectKey() + " has been already created");
            ResponseEntity<String> validityError = checkProjectValidity(teamKey, projectManagerKey, clientKey, projectTypeKey, value);
            if(validityError != null)
                return validityError;
            projectService.save(project);
            return AdviceController.responseOk("Project '" + project.getProjectKey() + "' saved.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/projects")
    public ResponseEntity<String> updateProject(@Valid Project project,
                                                @RequestParam(value="team") String teamKey ,
                                                @RequestParam(value="projectManager") String projectManagerKey,
                                                @RequestParam(value="client") String clientKey,
                                                @RequestParam(value="projectType") String projectTypeKey,
                                                @RequestParam(value="value") String value,
                                                BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            ResponseEntity<String> validityError = checkProjectValidity(teamKey, projectManagerKey, clientKey, projectTypeKey, value);
            if(validityError != null)
                return validityError;
            Project projectRepo = projectService.find(project.getProjectKey());
            if(projectRepo == null)
                return AdviceController.responseNotFound("Cannot update '" + project.getProjectKey() + "' project. (Project does not exists)");
            projectService.save(project);
            return AdviceController.responseOk("Project '" + project.getProjectKey() + "' updated.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/projects")
    public ResponseEntity<String> deleteProject(@Valid Project project,
                                                BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Project projectRepo = projectService.find(project.getProjectKey());
            if(projectRepo == null)
                return AdviceController.responseNotFound("Cannot delete '" + project.getProjectKey() + "' project. (Project does not exists)");
            projectService.deleteProjectByProject(projectRepo.getProjectKey());
            return AdviceController.responseOk("Project '" + projectRepo.getProjectKey() + "' deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete project '" + project.getProjectKey() + "'. (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    private ResponseEntity<String> checkProjectValidity(String teamKey, String projectManagerKey,
                                                        String clientKey, String projectTypeKey, String value){
        if(!AdviceController.isStringPositiveDecimal(value))
            return AdviceController.responseBadRequest("Value must be numeric");
        else if(teamService.findTeamByTeamName(teamKey) == null)
            return AdviceController.responseNotFound("Team '" + teamKey + "' not found");
        else if(userService.findUser(projectManagerKey) == null)
            return ResponseEntity.badRequest().body("ProjectManager '" + projectManagerKey + "' not found");
        else if(clientService.find(clientKey) == null)
            return AdviceController.responseNotFound("Client '" + clientKey + "' not found");
        else if(projectTypeService.find(projectTypeKey) == null)
            return AdviceController.responseNotFound("Project Type '" + projectTypeKey + "' not found");
        else return null;
    }
}
