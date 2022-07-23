package eu.rationence.pat.controller;

import eu.rationence.pat.model.User;
import eu.rationence.pat.service.ProjectActivityService;
import eu.rationence.pat.service.UserActivityService;
import eu.rationence.pat.service.UserService;
import eu.rationence.pat.model.ProjectActivity;
import eu.rationence.pat.model.Project;
import eu.rationence.pat.model.UserActivity;
import eu.rationence.pat.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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
    private static final String CLASS_DESC = "Activity";

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
                                                  @PathVariable String activityKey,
                                                  BindingResult result){
        try{
            ResponseEntity<String> validityError = checkUserActivityValidity(projectKey, activityKey, dailyRate);
            if(validityError != null)
                return validityError;
            User userRepo = userService.findUser(username);
            if(userRepo == null)
                return AdviceController.responseNotFound("Can't assign '" + username + "' to " + CLASS_DESC + " '" + activityKey + "'");
            userActivity.setC_Username(userRepo);
            userActivity.setUsername(userRepo.getUsername());
            UserActivity userActivityRepo = userActivityService.findUserActivity(activityKey, projectKey, userActivity.getC_Username().getUsername());
            if(userActivityRepo != null)
                return AdviceController.responseConflict("'" + userActivity.getC_Username().getUsername() + "' has already been assigned to " + CLASS_DESC + " '" + activityKey + "'");
            userActivity.setProject(projectKey);
            userActivity.setActivityKey(activityKey);
            userActivityService.save(userActivity);
            return AdviceController.responseOk("'" + username + "' has been assigned to " + CLASS_DESC + " '" + activityKey + "' successfully");
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
                                                     @PathVariable String activityKey,
                                                     BindingResult result){
        try{
            UserActivity userActivityRepo = userActivityService.findUserActivity(activityKey, projectKey, username);
            if(userActivityRepo == null)
                return AdviceController.responseNotFound("Can't update '" + username + "' to " + CLASS_DESC + " '" + activityKey + "'");
            ResponseEntity<String> validityError = checkUserActivityValidity(projectKey, activityKey, dailyRate);
            if(validityError != null)
                return validityError;
            User userRepo = userService.findUser(username);
            if(userRepo == null)
                return AdviceController.responseNotFound("Can't assign '" + username + "' to " + CLASS_DESC + " '" + activityKey + "'");
            userActivity.setC_Username(userRepo);
            userActivity.setC_Activity(projectActivityService.find(activityKey, projectKey));
            userActivityService.save(userActivity);
            return AdviceController.responseOk("'" + username + "' assignment to " + CLASS_DESC + " '" + activityKey + "' updated successfully.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/projects/{projectKey}/{activityKey}")
    public ResponseEntity<String> deleteUserActivity(@Valid UserActivity userActivity,
                                                     BindingResult result){
        try{
            UserActivity userActivityRepo = userActivityService.findUserActivity(userActivity.getActivityKey(), userActivity.getProject(), userActivity.getUsername());
            if(userActivityRepo == null)
                return AdviceController.responseNotFound("Can't remove '" + userActivity.getUsername() + "' from " + CLASS_DESC +" '" + userActivity.getActivityKey() + "'");

            userActivityService.delete(userActivity.getActivityKey(), userActivity.getProject(), userActivity.getUsername());
            return AdviceController.responseOk("User '" + userActivity.getUsername() + "' removed from " + CLASS_DESC + " '" + userActivity.getActivityKey() + "' successfully.");
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
            return AdviceController.responseNotFound(CLASS_DESC + " '" + activityKey + "' for Project '" + projectKey + "' does not exits");
        else return null;
    }
}
