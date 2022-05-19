package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.CompiledProjectActivityService;
import eu.Rationence.pat.service.CompiledStandardActivityService;
import eu.Rationence.pat.service.LocationService;
import eu.Rationence.pat.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;


@Controller
@AllArgsConstructor
public class TrackingController {
    private static final String ERROR_STR = "ERROR: ";

    @Autowired
    private final UserService userService;
    @Autowired
    private final CompiledProjectActivityService compiledProjectActivityService;
    @Autowired
    private final CompiledStandardActivityService compiledStandardActivityService;
    @Autowired
    private final LocationService locationService;

    @GetMapping ("/tracking")
    public String tracking(Model model, Principal principal) {
        LocalDate currentDate = LocalDate.now();
        return trackingMonthYear(currentDate.getYear(), currentDate.getMonthValue(), model, principal);
    }

    @GetMapping ("/tracking/{year}/{month}")
    public String trackingMonthYear(@PathVariable int year,
                                    @PathVariable int month,
                                    Model model, Principal principal) {
        model.addAttribute("locationList", locationService.findAll());
        LocalDate passedDate = LocalDate.of(year, month, 1);
        model.addAttribute("monthDays", passedDate.lengthOfMonth());
        model.addAttribute("monthName", passedDate.getMonth());
        model.addAttribute("userList", userService.findAll());
        String username = principal.getName();
        User userRepo = userService.findUserByUsername(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "tracking";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleBadRequestException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ERROR_STR + "Empty input or mismatched input type");
    }
}
