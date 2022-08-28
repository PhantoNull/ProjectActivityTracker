package eu.rationence.pat.controller;

import eu.rationence.pat.model.StandardActivity;
import eu.rationence.pat.model.User;
import eu.rationence.pat.model.dto.StandardActivityDTO;
import eu.rationence.pat.service.StandardActivityService;
import eu.rationence.pat.service.UserService;
import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;
    private final StandardActivityService standardActivityService;
    private final UserService userService;
    private static final String CLASS_DESC = "Standard Activity";

    @Autowired
    public StandardActivityController(ModelMapper modelMapper, StandardActivityService standardActivityService, UserService userService) {
        this.modelMapper = modelMapper;
        this.standardActivityService = standardActivityService;
        this.userService = userService;
    }

    @GetMapping ("/standardactivities")
    public String standardActivities(Model model, Principal principal) {
        model.addAttribute("stdActivityList", standardActivityService.findAll());
        String username = principal.getName();
        User userRepo = userService.find(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "standardactivities";
    }

    @PostMapping("/standardactivities")
    public ResponseEntity<String> addActivity(@Valid StandardActivityDTO stdActDTO,
                                                BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            StandardActivity stdAct = modelMapper.map(stdActDTO, StandardActivity.class);
            if(stdAct.getActivityKey().length() < 1)
                return AdviceController.responseBadRequest(CLASS_DESC + " Key Name can't be blank");
            else if(standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey()) != null)
                return AdviceController.responseConflict(stdAct.getActivityKey() + " has been already created");
            standardActivityService.save(stdAct);
            return AdviceController.responseOk(CLASS_DESC + " '" + stdAct.getActivityKey() + "' saved");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @PutMapping("/standardactivities")
    public ResponseEntity<String> updateActivity(@Valid StandardActivityDTO stdActDTO,
                                                    BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            StandardActivity stdAct = modelMapper.map(stdActDTO, StandardActivity.class);
            StandardActivity stdActRepo = standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey());
            if(stdActRepo == null)
                return AdviceController.responseNotFound(stdAct.getActivityKey() + " is not a valid activity");
            standardActivityService.save(stdAct);
            return AdviceController.responseOk(CLASS_DESC + " '" + stdAct.getActivityKey() + "' updated");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }

    @DeleteMapping("/standardactivities")
    public ResponseEntity<String> deleteActivity(@Valid StandardActivityDTO stdActDTO,
                                                    BindingResult result){
        try{
            if(result.hasErrors())
                return AdviceController.responseBadRequest(result.getAllErrors().toString());
            StandardActivity stdAct = modelMapper.map(stdActDTO, StandardActivity.class);
            StandardActivity stdActRepo = standardActivityService.findStandardActivityByActivityKey(stdAct.getActivityKey());
            if(stdActRepo == null)
                return AdviceController.responseNotFound(stdAct.getActivityKey() + " is not a valid activity");
            standardActivityService.delete(stdAct.getActivityKey());
            return AdviceController.responseOk("Team '" + stdAct.getActivityKey() + "' successfully deleted");
        }
        catch(DataIntegrityViolationException e){
            return AdviceController.responseForbidden("Cannot delete " + CLASS_DESC + " '" + stdActDTO.getActivityKey() + "' (Constraint violation)");
        }
        catch(Exception e){
            return AdviceController.responseServerError(e.getMessage());
        }
    }
}
