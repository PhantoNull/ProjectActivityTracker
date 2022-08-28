package eu.rationence.pat.controller;

import eu.rationence.pat.model.User;
import eu.rationence.pat.model.dto.ProjectActivityDTO;
import eu.rationence.pat.service.ProjectActivityService;
import eu.rationence.pat.service.UserService;
import eu.rationence.pat.model.Project;
import eu.rationence.pat.model.ProjectActivity;
import eu.rationence.pat.service.ActivityTypeService;
import eu.rationence.pat.service.ProjectService;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final ProjectService projectService;
    private final ActivityTypeService activityTypeService;
    private final ProjectActivityService projectActivityService;
    private static final String CLASS_DESC = "Activity";

    @Autowired
    public ProjectActivityController(ModelMapper modelMapper, UserService userService, ProjectService projectService, ActivityTypeService activityTypeService, ProjectActivityService projectActivityService) {
        this.modelMapper = modelMapper;
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
        User userRepo = userService.find(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "activities";
    }

    @PostMapping("/projects/{projectKey}")
    public ResponseEntity<String> addActivity(@Valid ProjectActivityDTO projectActivityDTO,
                                              @RequestParam(value="manDays") String manDays,
                                              @RequestParam(value="activityKey") String activityKey,
                                              @RequestParam(value="activityType") String activityType,
                                              @PathVariable String projectKey){
        try{
            ProjectActivity projectActivity = modelMapper.map(projectActivityDTO, ProjectActivity.class);
            ResponseEntity<String> validityError = checkActivityValidity(projectKey, activityType, manDays);
            if(validityError != null)
                return validityError;
            ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectKey);
            if(projectActivityRepo != null)
                return AdviceController.responseConflict(CLASS_DESC + " '" + projectActivity.getActivityKey() + "' has already been created");
            projectActivityService.save(projectActivity);
            return AdviceController.responseOk(CLASS_DESC + " '" + projectActivity.getActivityKey() + "' saved");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/projects/{projectKey}")
    public ResponseEntity<String> updateActivity(@Valid ProjectActivityDTO projectActivityDTO,
                                              @RequestParam(value="manDays") String manDays,
                                              @RequestParam(value="activityKey") String activityKey,
                                              @RequestParam(value="activityType") String activityType,
                                              @PathVariable String projectKey){
        try{
            ProjectActivity projectActivity = modelMapper.map(projectActivityDTO, ProjectActivity.class);
            ResponseEntity<String> validityError = checkActivityValidity(projectKey, activityType, manDays);
            if(validityError != null)
                return validityError;
            ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectKey);
            if(projectActivityRepo == null)
                return AdviceController.responseNotFound(CLASS_DESC + " " + projectActivity.getActivityKey() + " does not exits");
            projectActivity.setC_Project(projectService.find(projectKey));
            projectActivityService.save(projectActivity);
            return AdviceController.responseOk(CLASS_DESC + " '" + projectActivity.getActivityKey() + "' updated");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/projects/{projectKey}")
    public ResponseEntity<String> updateActivity(@Valid ProjectActivityDTO projectActivityDTO,
                                                 @RequestParam(value="activityKey") String activityKey,
                                                 @PathVariable String projectKey){
        try{
            ProjectActivity projectActivity = modelMapper.map(projectActivityDTO, ProjectActivity.class);
            Project projectRepo = projectService.find(projectKey);
            if(projectRepo == null)
                return AdviceController.responseNotFound("Project " + projectKey + " does not exits");
            ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectRepo.getProjectKey());
            if(projectActivityRepo == null)
                return AdviceController.responseNotFound(CLASS_DESC + " '" + projectActivity.getActivityKey() + "' does not exits");
            projectActivityService.delete(activityKey, projectRepo.getProjectKey());
            return AdviceController.responseOk(CLASS_DESC + " '" + projectActivity.getActivityKey() + "' successfully deleted");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete " + CLASS_DESC + " '" + projectActivityDTO.getActivityKey() + "' (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    private ResponseEntity<String> checkActivityValidity(String projectKey, String activityTypeKey, String manDays){
        if(!AdviceController.isStringPositiveDecimal(manDays))
            return AdviceController.responseBadRequest("Man Days value must be numeric");
        if(projectService.find(projectKey) == null)
            return AdviceController.responseNotFound("Project '" + projectKey + "' does not exits");
        else if(activityTypeService.find(activityTypeKey) == null)
            return AdviceController.responseNotFound(CLASS_DESC + " Type '" + activityTypeKey + "' does not exist");
        else return null;
    }
}
