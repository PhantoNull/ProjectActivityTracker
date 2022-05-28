package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.ProjectActivity;
import eu.Rationence.pat.model.Project;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.model.UserActivity;
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
import java.util.List;


@Controller
@AllArgsConstructor
public class UserActivityController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final ProjectService projectService;
    @Autowired
    private final ActivityTypeService activityTypeService;
    @Autowired
    private final ProjectActivityService projectActivityService;
    @Autowired
    private final UserActivityService userActivityService;

    @GetMapping("/projects/{projectKey}/{activityKey}")
    public String projectUserActivities(@PathVariable String projectKey,
                                        @PathVariable String activityKey,
                                        Model model, Principal principal){
        Project projectRepo = projectService.findProjectByProject(projectKey);
        ProjectActivity projectActivityRepo = projectActivityService.findActivityByActivityKeyAndProject(activityKey, projectRepo.getProjectKey());
        if(projectActivityRepo == null)
            return "error";
        List<User> availableUserList = userService.findAll();
        List<UserActivity> userActivityList = userActivityService.findUserActivitiesByProjectAndActivityKey(projectKey, activityKey);
        for(UserActivity ua : userActivityList)
            availableUserList.remove(ua.getC_Username());
        model.addAttribute("userList", availableUserList);
        model.addAttribute("userActivityList", userActivityList);
        model.addAttribute("activityKey", activityKey);
        model.addAttribute("projectKey", projectKey);

        String username = principal.getName();
        User userRepo = userService.findUserByUsername(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "userActivities";
    }

    @PostMapping("/projects/{projectKey}/{activityKey}")
    public ResponseEntity<String> addUserActivity(@Valid UserActivity userActivity,
                                                    @RequestParam(value="dailyRate") String dailyRate,
                                                    @RequestParam(value="username") String username,
                                                    @PathVariable String projectKey,
                                                    @PathVariable String activityKey){
        try{
            ResponseEntity<String> validityError = checkUserActivityValidity(projectKey, activityKey, dailyRate);
            if(validityError != null)
                return validityError;
            User userRepo = userService.findUserByUsername(username);
            if(userRepo == null)
                return ResponseEntity.status(409).body("ERROR: Can't assign '" + username + "' to activity '" + activityKey + "' (User does not exists)");
            userActivity.setC_Username(userRepo);
            userActivity.setUsername(userRepo.getUsername());
            UserActivity userActivityRepo = userActivityService.findUserActivityByActivityKeyAndProjectAndUsername(activityKey, projectKey, userActivity.getC_Username().getUsername());
            if(userActivityRepo != null)
                return ResponseEntity.status(409).body("ERROR: User '" + userActivity.getC_Username().getUsername() + "' has already been assigned to Activity '" + activityKey + "'");
            userActivity.setProject(projectKey);
            userActivity.setActivityKey(activityKey);
            userActivityService.saveUserActivity(userActivity);
            return ResponseEntity.ok("User '" + username + "' has been assigned to Activity '" + activityKey + "' successfully");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: Empty input or mismatched input type");
        }
    }

    @PutMapping("/projects/{projectKey}/{activityKey}")
    public ResponseEntity<String> updateUserActivity(@Valid UserActivity userActivity,
                                                     @RequestParam(value="dailyRate") String dailyRate,
                                                     @RequestParam(value="username") String username,
                                                     @PathVariable String projectKey,
                                                     @PathVariable String activityKey){
        try{
            UserActivity userActivityRepo = userActivityService.findUserActivityByActivityKeyAndProjectAndUsername(activityKey, projectKey, username);
            if(userActivityRepo == null)
                return ResponseEntity.status(409).body("ERROR: Can't update '" + username + "' to activity '" + activityKey + "' (User is not assigned)");
            ResponseEntity<String> validityError = checkUserActivityValidity(projectKey, activityKey, dailyRate);
            if(validityError != null)
                return validityError;
            User userRepo = userService.findUserByUsername(username);
            if(userRepo == null)
                return ResponseEntity.status(409).body("ERROR: Can't assign '" + username + "' to activity '" + activityKey + "' (User does not exists)");
            userActivity.setC_Username(userRepo);
            userActivity.setUsername(userRepo.getUsername());
            userActivity.setProject(projectKey);
            userActivity.setActivityKey(activityKey);
            userActivityService.saveUserActivity(userActivity);
            return ResponseEntity.ok("User '" + username + "' assignment to activity '" + activityKey + "' updated successfully.");
        }
        catch(Exception e){
            System.out.println(e);
            return ResponseEntity.badRequest()
                    .body("ERROR: Empty input or mismatched input type");
        }
    }

    @DeleteMapping("/projects/{projectKey}/{activityKey}")
    public ResponseEntity<String> deleteUserActivity(@Valid UserActivity userActivity,
                                                     @RequestParam(value="dailyRate") String dailyRate,
                                                     @RequestParam(value="username") String username,
                                                     @PathVariable String projectKey,
                                                     @PathVariable String activityKey){
        try{
            UserActivity userActivityRepo = userActivityService.findUserActivityByActivityKeyAndProjectAndUsername(activityKey, projectKey, username);
            if(userActivityRepo == null)
                return ResponseEntity.status(409).body("ERROR: Can't remove '" + username + "' from activity '" + activityKey + "' (User is not assigned)");
            ResponseEntity<String> validityError = checkUserActivityValidity(projectKey, activityKey, dailyRate);
            if(validityError != null)
                return validityError;
            User userRepo = userService.findUserByUsername(username);
            if(userRepo == null)
                return ResponseEntity.status(409).body("ERROR: Can't remove '" + username + " from activity '" + activityKey + "' (User does not exists)");
            userActivity.setC_Username(userRepo);
            userActivity.setUsername(userRepo.getUsername());
            userActivity.setProject(projectKey);
            userActivity.setActivityKey(activityKey);
            userActivityService.deleteUserActivityByActivityKeyAndProjectAndUsername(activityKey, projectKey, username);
            return ResponseEntity.ok("User '" + username + "' removed from activity '" + activityKey + "' successfully.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body("ERROR: " + e.getMessage());
        }
    }

    private ResponseEntity<String> checkUserActivityValidity(String projectKey, String activityKey, String dailyRate){
        if(dailyRate == null)
            return ResponseEntity.badRequest().body("ERROR: Daily Rate is a mandatory parameter and must be numeric");
        if(!isNumericString(dailyRate))
            return ResponseEntity.badRequest().body("ERROR: Daily Rate value must be numeric");
        if(projectService.findProjectByProject(projectKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Project '" + projectKey + "' does not exits");
        if(projectActivityService.findActivityByActivityKeyAndProject(activityKey, projectKey) == null)
            return ResponseEntity.badRequest().body("ERROR: Activity '" + activityKey + "' for Project '" + projectKey + "' does not exits");
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
