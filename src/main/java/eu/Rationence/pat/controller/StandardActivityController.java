package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.StandardActivity;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.StandardActivityService;
import eu.Rationence.pat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


@Controller
public class StandardActivityController {
    private final StandardActivityService standardActivityService;
    private final UserService userService;

    @Autowired
    public StandardActivityController(StandardActivityService standardActivityService, UserService userService) {
        this.standardActivityService = standardActivityService;
        this.userService = userService;
    }

    @GetMapping ("/standardactivities")
    public String teams(Model model, Principal principal) {
        model.addAttribute("stdActivityList", standardActivityService.findAll());
        String username = principal.getName();
        User userRepo = userService.findUser(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "standardactivities";
    }

    @PostMapping("/standardactivities")
    public ResponseEntity<String> addStdActivity(@Valid StandardActivity stdAct,
                                                BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            else if(stdAct.getActivityKey().length() < 1)
                return AdviceController.responseBadRequest("Activity Key Name can't be blank");
            else if(standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey()) != null)
                return AdviceController.responseConflict(stdAct.getActivityKey() + " has been already created");
            standardActivityService.save(stdAct);
            return AdviceController.responseOk("Standard Activity '" + stdAct.getActivityKey() + "' saved.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/standardactivities")
    public ResponseEntity<String> updateStdActivity(@Valid StandardActivity stdAct,
                                                    BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            StandardActivity stdActRepo = standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey());
            if(stdActRepo == null)
                return AdviceController.responseNotFound(stdAct.getActivityKey() + " is not a valid activity. (not found)");
            standardActivityService.save(stdAct);
            return AdviceController.responseOk("Standard Activity '" + stdAct.getActivityKey() + "' updated.");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/standardactivities")
    public ResponseEntity<String> deleteStdActivity(@Valid StandardActivity stdAct,
                                                    BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            StandardActivity stdActRepo = standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey());
            if(stdActRepo == null)
                return AdviceController.responseNotFound(stdAct.getActivityKey() + " is not a valid activity. (not found)");
            standardActivityService.delete(stdAct.getActivityKey());
            return AdviceController.responseOk("Team '" + stdAct.getActivityKey() + "' successfully deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete standard activity '" + stdAct.getActivityKey() + "'. (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }
}
