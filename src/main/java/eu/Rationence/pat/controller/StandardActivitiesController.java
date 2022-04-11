package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.StandardActivity;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.StandardActivityService;
import eu.Rationence.pat.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;


@Controller
@AllArgsConstructor
public class StandardActivitiesController {
    private static final String ERROR_STR = "ERROR: ";

    @Autowired
    private final StandardActivityService standardActivityService;
    @Autowired
    private final UserService userService;

    @GetMapping ("/standardactivities")
    public String teams(Model model, Principal principal) {
        model.addAttribute("stdActivitiesList", standardActivityService.findAll());
        String username = principal.getName();
        User userRepo = userService.findUserByUsername(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "teams";
    }

    @PostMapping("/standardactivities")
    public ResponseEntity<String> addTeam(@Valid StandardActivity stdAct,
                                          BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            else if(stdAct.getActivityKey().length() < 1)
                return ResponseEntity.badRequest().body(ERROR_STR + "Activity Key Name can't be blank");
            else if(standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey()) != null)
                return ResponseEntity.status(409).body(ERROR_STR + stdAct.getActivityKey() + " has been already created");
            standardActivityService.saveStdActivity(stdAct);
            return ResponseEntity.ok("Standard Activity '" + stdAct.getActivityKey() + "' saved.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @PutMapping("/standardactivities")
    public ResponseEntity<String> updateTeam(@Valid StandardActivity stdAct,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            StandardActivity stdActRepo = standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey());
            if(stdActRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + stdAct.getActivityKey() + " is not a valid activity. (not found)");
            standardActivityService.saveStdActivity(stdAct);
            return ResponseEntity.ok("Standard Activity '" + stdAct.getActivityKey() + "' updated.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @DeleteMapping("/standardactivities")
    public ResponseEntity<String> deleteTeam(@Valid StandardActivity stdAct,
                                             BindingResult result){
        try{
            if(result.hasErrors())
                return ResponseEntity.badRequest().body(ERROR_STR + result.getAllErrors());
            StandardActivity stdActRepo = standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey());
            if(stdActRepo == null)
                return ResponseEntity.status(404).body(ERROR_STR + stdAct.getActivityKey() + " is not a valid activity. (not found)");
            standardActivityService.deleteStandardActivityByStandardActivityKey(stdAct.getActivityKey());
            return ResponseEntity.ok("Team '" + stdAct.getActivityKey() + "' successfully deleted.");
        }
        catch(Exception e){
            return ResponseEntity.badRequest()
                    .body(ERROR_STR + e.getMessage());
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        System.out.println(e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ERROR_STR + "Empty input or mismatched input type");
    }
}
