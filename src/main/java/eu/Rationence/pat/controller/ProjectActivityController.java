package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.*;
import eu.Rationence.pat.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


@Controller
public class ProjectActivityController {
    private final UserService userService;
    private final ProjectService projectService;
    private final ActivityTypeService activityTypeService;
    private final ProjectActivityService projectActivityService;

    @Autowired
    public ProjectActivityController(UserService userService, ProjectService projectService, ActivityTypeService activityTypeService, ProjectActivityService projectActivityService) {
        this.userService = userService;
        this.projectService = projectService;
        this.activityTypeService = activityTypeService;
        this.projectActivityService = projectActivityService;
    }

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
                return AdviceController.responseConflict("Activity " + projectActivity.getActivityKey() + " has already been created");
            projectActivityService.save(projectActivity);
            return AdviceController.responseOk("Activity '" + projectActivity.getActivityKey() + "' saved.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
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
                return AdviceController.responseNotFound("Activity " + projectActivity.getActivityKey() + " does not exits.");
            projectActivity.setC_Project(projectService.find(projectKey));
            projectActivityService.save(projectActivity);
            return AdviceController.responseOk("Activity '" + projectActivity.getActivityKey() + "' updated.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/projects/{projectKey}")
    public ResponseEntity<String> updateActivity(@Valid ProjectActivity projectActivity,
                                                 @RequestParam(value="activityKey") String activityKey,
                                                 @PathVariable String projectKey){
        try{
            Project projectRepo = projectService.find(projectKey);
            if(projectRepo == null)
                return AdviceController.responseNotFound("Project " + projectKey + " does not exits.");
            ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectRepo.getProjectKey());
            if(projectActivityRepo == null)
                return AdviceController.responseNotFound("ERROR: Activity " + projectActivity.getActivityKey() + " does not exits.");
            projectActivityService.delete(activityKey, projectRepo.getProjectKey());
            return AdviceController.responseOk("Activity '" + projectActivity.getActivityKey() + "' successfully deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete project activity '" + projectActivity.getActivityKey() + "'. (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    private ResponseEntity<String> checkActivityValidity(String projectKey, String activityTypeKey, String manDays){
        if(!AdviceController.isStringPositiveDecimal(manDays))
            return AdviceController.responseBadRequest("Man Days value must be numeric");
        if(projectService.find(projectKey) == null)
            return AdviceController.responseNotFound("ERROR: Project '" + projectKey + "' does not exits");
        else if(activityTypeService.find(activityTypeKey) == null)
            return AdviceController.responseNotFound("ERROR: Activity Type '" + activityTypeKey + "' not found");
        else return null;
    }
}
