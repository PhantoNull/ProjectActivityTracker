package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.*;
import eu.Rationence.pat.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


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
    @Autowired
    private final ProjectActivityService projectActivityService;


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
    public ResponseEntity<String> addProject(Project project,
                                             @RequestParam(value="team") String teamKey ,
                                             @RequestParam(value="projectManager") String projectManagerKey,
                                             @RequestParam(value="client") String clientKey,
                                             @RequestParam(value="projectType") String projectTypeKey,
                                             @RequestParam(value="value") String value){
        try{
            if(projectService.find(project.getProjectKey()) != null)
                return ResponseEntity.status(409).body("ERROR: " + project.getProjectKey() + " has been already created");
            ResponseEntity<String> validityError = checkProjectValidity(project, teamKey, projectManagerKey, clientKey, projectTypeKey, value);
            if(validityError != null)
                return validityError;
            setProjectObjects(project, teamKey, projectManagerKey, clientKey, projectTypeKey);
            return ResponseEntity.ok("Project '" + project.getProjectKey() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PutMapping("/projects")
    public ResponseEntity<String> updateProject(@Valid Project project,
                                                @RequestParam(value="team") String teamKey ,
                                                @RequestParam(value="projectManager") String projectManagerKey,
                                                @RequestParam(value="client") String clientKey,
                                                @RequestParam(value="projectType") String projectTypeKey,
                                                @RequestParam(value="value") String value){
        try{
            ResponseEntity<String> validityError = checkProjectValidity(project, teamKey, projectManagerKey, clientKey, projectTypeKey, value);
            if(validityError != null)
                return validityError;
            Project projectRepo = projectService.find(project.getProjectKey());
            if(projectRepo == null)
                return ResponseEntity.status(409).body("ERROR: Cannot update '" + project.getProjectKey() + "' project. (Project does not exists)");
            setProjectObjects(project, teamKey, projectManagerKey, clientKey, projectTypeKey);
            return ResponseEntity.ok("Project '" + project.getProjectKey() + "' updated.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @DeleteMapping("/projects")
    public ResponseEntity<String> deleteProject(@Valid Project project){
        try{
            Project projectRepo = projectService.find(project.getProjectKey());
            if(projectRepo == null)
                return ResponseEntity.status(409).body("ERROR: Cannot delete '" + project.getProjectKey() + "' project. (Project does not exists)");
            projectService.deleteProjectByProject(projectRepo.getProjectKey());
            return ResponseEntity.ok("Project '" + projectRepo.getProjectKey() + "' deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete project '" + project.getProjectKey() + "'. (Constraint violation)");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    private ResponseEntity<String> checkProjectValidity(Project project, String teamKey, String projectManagerKey,
                                                        String clientKey, String projectTypeKey, String value){
        if(!isNumericString(value))
            return ResponseEntity.badRequest().body("ERROR: Value must be numeric");
        if(teamService.findTeamByTeamName(teamKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Team '" + teamKey + "' not found");
        else if(userService.findUser(projectManagerKey) == null)
            return ResponseEntity.badRequest().body("ERROR: ProjectManager '" + projectManagerKey + "' not found");
        else if(clientService.find(clientKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Client '" + clientKey + "' not found");
        else if(projectTypeService.find(projectTypeKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Project Type '" + projectTypeKey + "' not found");
        else return null;
    }

    private void setProjectObjects(@Valid Project project,
                                   @RequestParam("team") String teamKey,
                                   @RequestParam("projectManager") String projectManagerKey,
                                   @RequestParam("client") String clientKey,
                                   @RequestParam("projectType") String projectTypeKey) {
        Team teamRepo = teamService.findTeamByTeamName(teamKey);
        User userRepo = userService.findUser(projectManagerKey);
        Client clientRepo = clientService.find(clientKey);
        ProjectType projectTypeRepo = projectTypeService.find(projectTypeKey);
        project.setProjectManager(userRepo);
        project.setTeam(teamRepo);
        project.setClient(clientRepo);
        project.setProjectType(projectTypeRepo);
        projectService.save(project);
    }

    private boolean isNumericString(String string){
        for (int i=0; i< string.length(); i++){
            if("0123456789".indexOf(string.charAt(i)) == -1)
                return false;
        }
        return true;
    }
}
