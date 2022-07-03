package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.ProjectActivity;
import eu.Rationence.pat.model.Project;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.model.UserActivity;
import eu.Rationence.pat.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@Controller
public class UserActivityController {
    private final UserService userService;
    private final ProjectService projectService;
    private final ProjectActivityService projectActivityService;
    private final UserActivityService userActivityService;

    @Autowired
    public UserActivityController(UserService userService, ProjectService projectService, ProjectActivityService projectActivityService, UserActivityService userActivityService) {
        this.userService = userService;
        this.projectService = projectService;
        this.projectActivityService = projectActivityService;
        this.userActivityService = userActivityService;
    }

    @GetMapping("/projects/{projectKey}/{activityKey}")
    public String projectUserActivities(@PathVariable String projectKey,
                                        @PathVariable String activityKey,
                                        Model model, Principal principal){
        Project projectRepo = projectService.find(projectKey);
        ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectRepo.getProjectKey());
        if(projectActivityRepo == null)
            return "error";
        List<User> availableUserList = userService.findAll();
        List<UserActivity> userActivityList = userActivityService.findUserActivities(projectKey, activityKey);
        for(UserActivity ua : userActivityList)
            availableUserList.remove(ua.getC_Username());
        model.addAttribute("userList", availableUserList);
        model.addAttribute("userActivityList", userActivityList);
        model.addAttribute("activityKey", activityKey);
        model.addAttribute("projectKey", projectKey);

        String username = principal.getName();
        User userRepo = userService.findUser(username);
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
            User userRepo = userService.findUser(username);
            if(userRepo == null)
                return AdviceController.responseNotFound("Can't assign '" + username + "' to activity '" + activityKey + "' (User not found)");
            userActivity.setC_Username(userRepo);
            userActivity.setUsername(userRepo.getUsername());
            UserActivity userActivityRepo = userActivityService.findUserActivity(activityKey, projectKey, userActivity.getC_Username().getUsername());
            if(userActivityRepo != null)
                return AdviceController.responseConflict("User '" + userActivity.getC_Username().getUsername() + "' has already been assigned to Activity '" + activityKey + "'");
            userActivity.setProject(projectKey);
            userActivity.setActivityKey(activityKey);
            userActivityService.save(userActivity);
            return AdviceController.responseOk("User '" + username + "' has been assigned to Activity '" + activityKey + "' successfully");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/projects/{projectKey}/{activityKey}")
    public ResponseEntity<String> updateUserActivity(@Valid UserActivity userActivity,
                                                     @RequestParam(value="dailyRate") String dailyRate,
                                                     @RequestParam(value="username") String username,
                                                     @PathVariable String projectKey,
                                                     @PathVariable String activityKey){
        try{
            UserActivity userActivityRepo = userActivityService.findUserActivity(activityKey, projectKey, username);
            if(userActivityRepo == null)
                return AdviceController.responseNotFound("Can't update '" + username + "' to activity '" + activityKey + "' (User is not assigned)");
            ResponseEntity<String> validityError = checkUserActivityValidity(projectKey, activityKey, dailyRate);
            if(validityError != null)
                return validityError;
            User userRepo = userService.findUser(username);
            if(userRepo == null)
                return AdviceController.responseNotFound("Can't assign '" + username + "' to activity '" + activityKey + "' (User does not exists)");
            userActivity.setC_Username(userRepo);
            userActivity.setUsername(userRepo.getUsername());
            userActivity.setProject(projectKey);
            userActivity.setActivityKey(activityKey);
            userActivityService.save(userActivity);
            return AdviceController.responseOk("User '" + username + "' assignment to activity '" + activityKey + "' updated successfully.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/projects/{projectKey}/{activityKey}")
    public ResponseEntity<String> deleteUserActivity(@Valid UserActivity userActivity,
                                                     @RequestParam(value="dailyRate") String dailyRate,
                                                     @RequestParam(value="username") String username,
                                                     @PathVariable String projectKey,
                                                     @PathVariable String activityKey){
        try{
            UserActivity userActivityRepo = userActivityService.findUserActivity(activityKey, projectKey, username);
            if(userActivityRepo == null)
                return AdviceController.responseNotFound("Can't remove '" + username + "' from activity '" + activityKey + "' (User is not assigned)");
            ResponseEntity<String> validityError = checkUserActivityValidity(projectKey, activityKey, dailyRate);
            if(validityError != null)
                return validityError;
            User userRepo = userService.findUser(username);
            if(userRepo == null)
                return AdviceController.responseNotFound("Can't remove '" + username + " from activity '" + activityKey + "' (User does not exists)");
            userActivity.setC_Username(userRepo);
            userActivity.setUsername(userRepo.getUsername());
            userActivity.setProject(projectKey);
            userActivity.setActivityKey(activityKey);
            userActivityService.delete(activityKey, projectKey, username);
            return AdviceController.responseOk("User '" + username + "' removed from activity '" + activityKey + "' successfully.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    private ResponseEntity<String> checkUserActivityValidity(String projectKey, String activityKey, String dailyRate){
        if(dailyRate == null)
            return AdviceController.responseBadRequest("Daily Rate is a mandatory parameter and must be numeric");
        if(!AdviceController.isStringPositiveDecimal(dailyRate))
            return AdviceController.responseBadRequest("Daily Rate value must be numeric");
        if(projectService.find(projectKey) == null)
            return AdviceController.responseNotFound("Project '" + projectKey + "' does not exits");
        if(projectActivityService.find(activityKey, projectKey) == null)
            return AdviceController.responseNotFound("Activity '" + activityKey + "' for Project '" + projectKey + "' does not exits");
        else return null;
    }
}
