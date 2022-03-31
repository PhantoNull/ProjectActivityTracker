package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.*;
import eu.Rationence.pat.service.*;
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
                                             @RequestParam(value="client") String client){
        try{
            Team teamRepo = teamService.findTeamByTeamName(teamKey);
            User userRepo = userService.findUtenteByUsername(projectManagerKey);
            Client clientRepo = clientService.findClientByClient(client);
            project.setProjectManager(userRepo);
            project.setTeam(teamRepo);
            project.setClient(clientRepo);
            projectService.saveProject(project);
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
                                             @RequestParam(value="client") String client){
        try{
            Project projectRepo = projectService.findProjectByProject(project.getProject());
            Team teamRepo = teamService.findTeamByTeamName(teamKey);
            User userRepo = userService.findUtenteByUsername(projectManagerKey);
            Client clientRepo = clientService.findClientByClient(client);
            projectRepo.setProjectManager(userRepo);
            projectRepo.setTeam(teamRepo);
            projectRepo.setClient(clientRepo);
            projectService.saveProject(projectRepo);
            return ResponseEntity.ok("Project '" + projectRepo.getProject() + "' updated.");
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
            projectService.deleteProjectByProject(projectRepo.getProject());
            return ResponseEntity.ok("Project '" + projectRepo.getProject() + "' deleted.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }


}
