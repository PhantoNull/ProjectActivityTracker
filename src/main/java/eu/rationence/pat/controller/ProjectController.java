package eu.rationence.pat.controller;

import eu.rationence.pat.model.User;
import eu.rationence.pat.model.Project;
import eu.rationence.pat.model.dto.ProjectDTO;
import eu.rationence.pat.service.*;
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


@Controller
public class ProjectController {
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final ProjectTypeService projectTypeService;
    private final ClientService clientService;
    private final ProjectActivityService projectActivityService;
    private static final String CLASS_DESC = "Project";

    @Autowired
    public ProjectController(ModelMapper modelMapper, UserService userService, TeamService teamService, ProjectService projectService, ProjectTypeService projectTypeService, ClientService clientService, ProjectActivityService projectActivityService) {
        this.modelMapper = modelMapper;
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
        User userRepo = userService.find(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "projects";
    }

    @PostMapping("/projects")
    public ResponseEntity<String> addProject(@Valid ProjectDTO projectDTO,
                                             @RequestParam(value="team") String teamKey ,
                                             @RequestParam(value="projectManager") String projectManagerKey,
                                             @RequestParam(value="client") String clientKey,
                                             @RequestParam(value="projectType") String projectTypeKey,
                                             @RequestParam(value="value") String value,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Project project = modelMapper.map(projectDTO, Project.class);
            if(projectService.find(project.getProjectKey()) != null)
                return AdviceController.responseConflict(project.getProjectKey() + " has been already created");
            ResponseEntity<String> validityError = checkProjectValidity(teamKey, projectManagerKey, clientKey, projectTypeKey, value);
            if(validityError != null)
                return validityError;
            projectService.save(project);
            return AdviceController.responseOk(CLASS_DESC + " '" + project.getProjectKey() + "' saved");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/projects")
    public ResponseEntity<String> updateProject(@Valid ProjectDTO projectDTO,
                                                @RequestParam(value="team") String teamKey ,
                                                @RequestParam(value="projectManager") String projectManagerKey,
                                                @RequestParam(value="client") String clientKey,
                                                @RequestParam(value="projectType") String projectTypeKey,
                                                @RequestParam(value="value") String value,
                                                BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Project project = modelMapper.map(projectDTO, Project.class);
            ResponseEntity<String> validityError = checkProjectValidity(teamKey, projectManagerKey, clientKey, projectTypeKey, value);
            if(validityError != null)
                return validityError;
            Project projectRepo = projectService.find(project.getProjectKey());
            if(projectRepo == null)
                return AdviceController.responseNotFound("Cannot update " + CLASS_DESC + " '" + project.getProjectKey() + "'");
            projectService.save(project);
            return AdviceController.responseOk(CLASS_DESC + " '" + project.getProjectKey() + "' updated");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/projects")
    public ResponseEntity<String> deleteProject(@Valid ProjectDTO projectDTO,
                                                BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            Project project = modelMapper.map(projectDTO, Project.class);
            Project projectRepo = projectService.find(project.getProjectKey());
            if(projectRepo == null)
                return AdviceController.responseNotFound("Cannot delete " + CLASS_DESC + " '" + project.getProjectKey() + "'");
            projectService.deleteProjectByProject(projectRepo.getProjectKey());
            return AdviceController.responseOk(CLASS_DESC + " '" + projectRepo.getProjectKey() + "' deleted");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete " + CLASS_DESC + " '" + projectDTO.getProjectKey() + "' (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    private ResponseEntity<String> checkProjectValidity(String teamKey, String projectManagerKey,
                                                        String clientKey, String projectTypeKey, String value){
        if(!AdviceController.isStringPositiveDecimal(value))
            return AdviceController.responseBadRequest("Value must be numeric");
        else if(teamService.find(teamKey) == null)
            return AdviceController.responseNotFound("Team '" + teamKey + "' does not exist");
        else if(userService.find(projectManagerKey) == null)
            return ResponseEntity.badRequest().body(CLASS_DESC + " Manager '" + projectManagerKey + "' is invalid");
        else if(clientService.find(clientKey) == null)
            return AdviceController.responseNotFound("Client '" + clientKey + "' not found");
        else if(projectTypeService.find(projectTypeKey) == null)
            return AdviceController.responseNotFound(CLASS_DESC +  " Type '" + projectTypeKey + "' is invalid");
        else return null;
    }
}
