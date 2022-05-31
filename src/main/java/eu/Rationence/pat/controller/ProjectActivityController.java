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
public class ProjectActivityController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final ProjectService projectService;
    @Autowired
    private final ActivityTypeService activityTypeService;
    @Autowired
    private final ProjectActivityService projectActivityService;

    @GetMapping("/projects/{projectKey}")
    public String projectActivities(@PathVariable String projectKey, Model model, Principal principal){
        Project projectRepo = projectService.find(projectKey);
        if(projectRepo == null)
            return "error";
        for (ProjectActivity projectActivity : projectActivityService.findActivitiesByProject(projectRepo.getProjectKey())) {
            int resourceNumber = projectActivity.getUserActivities().size();
            model.addAttribute(projectActivity.getActivityKey()+"Resources",resourceNumber);
        }
        model.addAttribute("activityTypeList", activityTypeService.findAll());
        model.addAttribute("activityList", projectActivityService.findActivitiesByProject(projectRepo.getProjectKey()));
        model.addAttribute("projectKey", projectKey);
        String username = principal.getName();
        User userRepo = userService.findUser(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "activities";
    }

    @PostMapping("/projects/{projectKey}")
    public ResponseEntity<String> addActivity(@Valid ProjectActivity projectActivity,
                                              @RequestParam(value="manDays") String manDays,
                                              @RequestParam(value="activityKey") String activityKey,
                                              @RequestParam(value="activityType") String activityType,
                                              @PathVariable String projectKey){
        try{
            ResponseEntity<String> validityError = checkActivityValidity(projectKey, activityType, manDays);
            if(validityError != null)
                return validityError;
            ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectKey);
            if(projectActivityRepo != null)
                return ResponseEntity.status(409).body("ERROR: Activity " + projectActivity.getActivityKey() + " has already been created");
            projectActivity.setProject(projectKey);
            projectActivityService.save(projectActivity);
            return ResponseEntity.ok("Activity '" + projectActivity.getActivityKey() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PutMapping("/projects/{projectKey}")
    public ResponseEntity<String> updateActivity(@Valid ProjectActivity projectActivity,
                                              @RequestParam(value="manDays") String manDays,
                                              @RequestParam(value="activityKey") String activityKey,
                                              @RequestParam(value="activityType") String activityType,
                                              @PathVariable String projectKey){
        try{
            ResponseEntity<String> validityError = checkActivityValidity(projectKey, activityType, manDays);
            if(validityError != null)
                return validityError;
            ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectKey);
            if(projectActivityRepo == null)
                return ResponseEntity.status(409).body("ERROR: Activity " + projectActivity.getActivityKey() + " does not exits.");
            projectActivity.setProject(projectKey);
            projectActivityService.save(projectActivity);
            return ResponseEntity.ok("Activity '" + projectActivity.getActivityKey() + "' updated.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @DeleteMapping("/projects/{projectKey}")
    public ResponseEntity<String> updateActivity(@Valid ProjectActivity projectActivity,
                                                 @RequestParam(value="activityKey") String activityKey,
                                                 @PathVariable String projectKey){
        try{
            Project projectRepo = projectService.find(projectKey);
            if(projectRepo == null)
                return ResponseEntity.status(409).body("ERROR: Project " + projectKey + " does not exits.");
            ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectRepo.getProjectKey());
            if(projectActivityRepo == null)
                return ResponseEntity.status(409).body("ERROR: Activity " + projectActivity.getActivityKey() + " does not exits.");
            projectActivityService.delete(activityKey, projectRepo.getProjectKey());
            return ResponseEntity.ok("Activity '" + projectActivity.getActivityKey() + "' successfully deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete project activity '" + projectActivity.getActivityKey() + "'. (Constraint violation)");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    private ResponseEntity<String> checkActivityValidity(String projectKey, String activityTypeKey, String manDays){
        if(!isNumericString(manDays))
            return ResponseEntity.badRequest().body("ERROR: Man Days value must be numeric");
        if(projectService.find(projectKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Project '" + projectKey + "' does not exits");
        else if(activityTypeService.find(activityTypeKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Activity Type '" + activityTypeKey + "' not found");
        else return null;
    }

    private boolean isNumericString(String string){
        for (int i=0; i< string.length(); i++){
            if("0123456789".indexOf(string.charAt(i)) == -1)
                return false;
        }
        return true;
    }
}
