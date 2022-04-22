package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.*;
import eu.Rationence.pat.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


@Controller
@AllArgsConstructor
public class ActivityController {
    @Autowired
    private final ProjectService projectService;
    @Autowired
    private final ActivityTypeService activityTypeService;
    @Autowired
    private final ActivityService activityService;

    @GetMapping("/projects/{projectKey}")
    public String projectActivities(@PathVariable String projectKey, Model model, Principal principal){
        Project projectRepo = projectService.findProjectByProject(projectKey);
        if(projectRepo == null)
            return "error";
        for (Activity activity:activityService.findActivitiesByProject(projectRepo)) {
            int resourceNumber = activity.getUsers().size();
            model.addAttribute(activity.getActivityKey()+"Resources",resourceNumber);
        }
        model.addAttribute("activityTypeList", activityTypeService.findAll());
        model.addAttribute("activityList", activityService.findActivitiesByProject(projectRepo));
        model.addAttribute("projectKey", projectKey);
        return "activities";
    }

    @PostMapping("/projects/{projectKey}")
    public ResponseEntity<String> addActivity(@Valid Activity activity,
                                              @RequestParam(value="manDays") String manDays,
                                              @RequestParam(value="activityKey") String activityKey,
                                              @RequestParam(value="activityType") String activityType,
                                              @PathVariable String projectKey){
        try{
            ResponseEntity<String> validityError = checkActivityValidity(projectKey, activityType, manDays);
            if(validityError != null)
                return validityError;
            Activity activityRepo = activityService.findActivityByActivityKeyAndProject(activityKey, projectService.findProjectByProject(projectKey));
            if(activityRepo != null)
                return ResponseEntity.status(409).body("ERROR: Activity " + activity.getActivityKey() + " has been already created");
            activity.setProjectId(projectKey);
            activityService.saveActivity(activity);
            return ResponseEntity.ok("Activity '" + activity.getActivityKey() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @PutMapping("/projects/{projectKey}")
    public ResponseEntity<String> updateActivity(@Valid Activity activity,
                                              @RequestParam(value="manDays") String manDays,
                                              @RequestParam(value="activityKey") String activityKey,
                                              @RequestParam(value="activityType") String activityType,
                                              @PathVariable String projectKey){
        try{
            ResponseEntity<String> validityError = checkActivityValidity(projectKey, activityType, manDays);
            if(validityError != null)
                return validityError;
            Activity activityRepo = activityService.findActivityByActivityKeyAndProject(activityKey, projectService.findProjectByProject(projectKey));
            if(activityRepo == null)
                return ResponseEntity.status(409).body("ERROR: Activity " + activity.getActivityKey() + " does not exits.");
            activity.setProjectId(projectKey);
            activityService.saveActivity(activity);
            return ResponseEntity.ok("Activity '" + activity.getActivityKey() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    @DeleteMapping("/projects/{projectKey}")
    public ResponseEntity<String> updateActivity(@Valid Activity activity,
                                                 @RequestParam(value="activityKey") String activityKey,
                                                 @PathVariable String projectKey){
        try{
            Project projectRepo = projectService.findProjectByProject(projectKey);
            if(projectRepo == null)
                return ResponseEntity.status(409).body("ERROR: Project " + projectKey + " does not exits.");
            Activity activityRepo = activityService.findActivityByActivityKeyAndProject(activityKey, projectRepo);
            if(activityRepo == null)
                return ResponseEntity.status(409).body("ERROR: Activity " + activity.getActivityKey() + " does not exits.");
            activityService.deleteActivityByActivityKeyAndProject(activityKey, projectRepo);
            return ResponseEntity.ok("Activity '" + activity.getActivityKey() + "' successfully deleted.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    private ResponseEntity<String> checkActivityValidity(String projectKey, String activityTypeKey, String manDays){
        if(!isNumericString(manDays))
            return ResponseEntity.badRequest().body("ERROR: Man Days value must be numeric");
        if(projectService.findProjectByProject(projectKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Project '" + projectKey + "' does not exits");
        else if(activityTypeService.findActivityTypeByActivityType(activityTypeKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Activity Type '" + activityTypeKey + "' not found");
        else return null;
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        System.out.println(e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("ERROR: Empty input or mismatched input type");
    }

    private boolean isNumericString(String string){
        for (int i=0; i< string.length(); i++){
            if("0123456789".indexOf(string.charAt(i)) == -1)
                return false;
        }
        return true;
    }
}
