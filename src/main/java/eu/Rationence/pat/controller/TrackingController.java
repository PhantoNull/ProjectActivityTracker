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

import java.security.Principal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;


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
    private final ProjectActivityService projectActivityService;
    @Autowired
    private final StandardActivityService standardActivityService;
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
        if(passedDate.isAfter(LocalDate.now()))
            return "error";
        boolean lastCompilableSheet = month == LocalDate.now().getMonthValue() && year == LocalDate.now().getYear();
        model.addAttribute("nextAvailable", lastCompilableSheet);
        model.addAttribute("year", year);
        model.addAttribute("monthDays", passedDate.lengthOfMonth());
        model.addAttribute("monthName", passedDate.getMonth());
        int previousMonth = month-1, previousYear = year, nextMonth = month + 1, nextYear = year;
        if(month == 1) {
            previousMonth = 12;
            previousYear = year - 1;
        }
        else if(month == 12){
            nextMonth = 1;
            nextYear = year + 1;
        }
        model.addAttribute("previousMonth", previousMonth);
        model.addAttribute("previousYear", previousYear);
        model.addAttribute("nextMonth", nextMonth);
        model.addAttribute("nextYear", nextYear);

        String username = principal.getName();
        User userRepo = userService.findUserByUsername(username);

        List<CompiledProjectActivity> compiledProjectActivityList = compiledProjectActivityService
                .findActivitiesByUsernameAndMonthAndYear(username, month, year);
        HashSet<CompiledProjectActivityRow> projectActivityHashSet = new HashSet<>();
        for(CompiledProjectActivity compiledProjectActivity : compiledProjectActivityList){
            projectActivityHashSet.add(CompiledProjectActivityRow.builder()
                    .project(compiledProjectActivity.getProject())
                    .activityKey(compiledProjectActivity.getActivityKey())
                    .location(compiledProjectActivity.getLocationName()).build());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(compiledProjectActivity.getDate());
            model.addAttribute(compiledProjectActivity.getProject() + "."
                                + compiledProjectActivity.getActivityKey() + "."
                                + compiledProjectActivity.getLocationName() + "."
                                + calendar.get(Calendar.DAY_OF_MONTH), compiledProjectActivity.getHours());
        }
        model.addAttribute("projectActivityList", projectActivityHashSet);
        System.out.println(projectActivityHashSet);

        List<CompiledStandardActivity> compiledStandardActivityList = compiledStandardActivityService
                .findActivitiesByUsernameAndMonthAndYear(username, month, year);
        HashSet<CompiledStandardActivityRow> standardActivityHashSet = new HashSet<>();
        for(CompiledStandardActivity compiledStandardActivity : compiledStandardActivityList){
            standardActivityHashSet.add(CompiledStandardActivityRow.builder()
                    .activityKey(compiledStandardActivity.getActivityKey())
                    .location(compiledStandardActivity.getLocationName()).build());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(compiledStandardActivity.getDate());
            model.addAttribute("Standard."
                    + compiledStandardActivity.getActivityKey() + "."
                    + compiledStandardActivity.getLocationName() + "."
                    + calendar.get(Calendar.DAY_OF_MONTH), compiledStandardActivity.getHours());
        }
        if(standardActivityHashSet != null)
            model.addAttribute("standardActivityList", standardActivityHashSet);

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
