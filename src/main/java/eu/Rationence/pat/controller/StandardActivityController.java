package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.StandardActivity;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.StandardActivityService;
import eu.Rationence.pat.service.UserService;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class StandardActivityController {
    private static final String ERROR_STR = "ERROR: ";

    @Autowired
    private final StandardActivityService standardActivityService;
    @Autowired
    private final UserService userService;

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
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            else if(stdAct.getActivityKey().length() < 1)
                return ResponseEntity.badRequest().body(ERROR_STR + "Activity Key Name can't be blank");
            else if(standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey()) != null)
                return ResponseEntity.status(409).body(ERROR_STR + stdAct.getActivityKey() + " has been already created");
            standardActivityService.save(stdAct);
            return ResponseEntity.ok("Standard Activity '" + stdAct.getActivityKey() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @PutMapping("/standardactivities")
    public ResponseEntity<String> updateStdActivity(@Valid StandardActivity stdAct,
                                                    BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            StandardActivity stdActRepo = standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey());
            if(stdActRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + stdAct.getActivityKey() + " is not a valid activity. (not found)");
            standardActivityService.save(stdAct);
            return ResponseEntity.ok("Standard Activity '" + stdAct.getActivityKey() + "' updated.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @DeleteMapping("/standardactivities")
    public ResponseEntity<String> deleteStdActivity(@Valid StandardActivity stdAct,
                                                    BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            StandardActivity stdActRepo = standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey());
            if(stdActRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + stdAct.getActivityKey() + " is not a valid activity. (not found)");
            standardActivityService.delete(stdAct.getActivityKey());
            return ResponseEntity.ok("Team '" + stdAct.getActivityKey() + "' successfully deleted.");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete standard activity '" + stdAct.getActivityKey() + "'. (Constraint violation)");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }
}
