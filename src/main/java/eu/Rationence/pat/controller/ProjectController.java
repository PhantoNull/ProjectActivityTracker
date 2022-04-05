package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.*;
import eu.Rationence.pat.service.*;
import lombok.AllArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@Controller
@AllArgsConstructor
public class ProjectController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final TeamService teamService;
    @Autowired
    private final ProjectService projectService;
    @Autowired
    private final ProjectTypeService projectTypeService;
    @Autowired
    private final ClientService clientService;


    @GetMapping ("/projects")
    public String projects(Model model) {
        model.addAttribute("listaUtenti", userService.findAll());
        model.addAttribute("listaTeams", teamService.findAll());
        model.addAttribute("listaProgetti", projectService.findAll());
        model.addAttribute("listaTipoProgetti", projectTypeService.findAll());
        model.addAttribute("listaClienti", clientService.findAll());
        return "projects";
    }

    @PostMapping("/addProject")
    public ResponseEntity<String> addProject(@Valid Project project,
                                             @RequestParam(value="team") String teamKey ,
                                             @RequestParam(value="projectManager") String projectManagerKey,
                                             @RequestParam(value="client") String clientKey,
                                             @RequestParam(value="projectType") String projectTypeKey){
        try{
            if(projectService.findProjectByProject(project.getProject()) != null)
                return ResponseEntity.status(409).body("ERROR: " + project.getProject() + " has been already created");
            ResponseEntity<String> validityError = checkProjectValidity(project, teamKey, projectManagerKey, clientKey, projectTypeKey);
            if(validityError != null)
                return validityError;
            setProjectObjects(project, teamKey, projectManagerKey, clientKey, projectTypeKey);
            return ResponseEntity.ok("Project '" + project.getProject() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/updateProject")
    public ResponseEntity<String> updateProject(@Valid Project project,
                                                @RequestParam(value="team") String teamKey ,
                                                @RequestParam(value="projectManager") String projectManagerKey,
                                                @RequestParam(value="client") String clientKey,
                                                @RequestParam(value="projectType") String projectTypeKey){
        try{
            ResponseEntity<String> validityError = checkProjectValidity(project, teamKey, projectManagerKey, clientKey, projectTypeKey);
            if(validityError != null)
                return validityError;
            Project projectRepo = projectService.findProjectByProject(project.getProject());
            setProjectObjects(project, teamKey, projectManagerKey, clientKey, projectTypeKey);
            return ResponseEntity.ok("Project '" + project.getProject() + "' updated.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PostMapping("/deleteProject")
    public ResponseEntity<String> deleteProject(@Valid Project project){
        try{
            Project projectRepo = projectService.findProjectByProject(project.getProject());
            if(projectRepo == null)
                return ResponseEntity.status(409).body("ERROR: Cannot delete '" + project.getProject() + "' project. (Project does not exists)");
            projectService.deleteProjectByProject(projectRepo.getProject());
            return ResponseEntity.ok("Project '" + projectRepo.getProject() + "' deleted.");
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

    private ResponseEntity<String> checkProjectValidity(Project project, String teamKey, String projectManagerKey,
                                                        String clientKey, String projectTypeKey){
        if(teamService.findTeamByTeamName(teamKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Team '" + teamKey + "' not found");
        else if(userService.findUserByUsername(projectManagerKey) == null)
            return ResponseEntity.badRequest().body("ERROR: ProjectManager '" + projectManagerKey + "' not found");
        else if(clientService.findClientByClient(clientKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Client '" + clientKey + "' not found");
        else if(projectTypeService.findProjectTypeByProjectType(projectTypeKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Project Type '" + projectTypeKey + "' not found");
        else return null;
    }

    private void setProjectObjects(@Valid Project project,
                                   @RequestParam("team") String teamKey,
                                   @RequestParam("projectManager") String projectManagerKey,
                                   @RequestParam("client") String clientKey,
                                   @RequestParam("projectType") String projectTypeKey) {
        Team teamRepo = teamService.findTeamByTeamName(teamKey);
        User userRepo = userService.findUserByUsername(projectManagerKey);
        Client clientRepo = clientService.findClientByClient(clientKey);
        ProjectType projectTypeRepo = projectTypeService.findProjectTypeByProjectType(projectTypeKey);
        project.setProjectManager(userRepo);
        project.setTeam(teamRepo);
        project.setClient(clientRepo);
        project.setProjectType(projectTypeRepo);
        projectService.saveProject(project);
    }
}
