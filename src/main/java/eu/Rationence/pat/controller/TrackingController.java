package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.*;
import eu.Rationence.pat.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
    private final ProjectService projectService;
    @Autowired
    private final ProjectActivityService projectActivityService;
    @Autowired
    private final StandardActivityService standardActivityService;
    @Autowired
    private final LocationService locationService;

    final int[][] festivity = {{1,1}, {6,1}, {25,4}, {1,5}, {2,6}, {15,8}, {1,11}, {8,12}, {25,12}, {26,12}};

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
        model.addAttribute("month", month);
        model.addAttribute("monthDays", passedDate.lengthOfMonth());
        model.addAttribute("monthName", passedDate.getMonth());

        Set<Integer> weekendDaysSet = new HashSet<>();

        for(int i=1; i <= passedDate.lengthOfMonth(); i++){
            LocalDate cycleLocalDate = LocalDate.of(year, month, i);
            if(cycleLocalDate.getDayOfWeek() == DayOfWeek.SATURDAY || cycleLocalDate.getDayOfWeek() == DayOfWeek.SUNDAY)
                weekendDaysSet.add(i);
        }
        for(int i=0; i<festivity.length; i++){
            if(festivity[i][1] == month){
                weekendDaysSet.add(festivity[i][0]);
            }
        }
        model.addAttribute("weekendDays", weekendDaysSet);

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


        Set<UserActivity> activitySetRepo = userRepo.getActivities();
        for(UserActivity userActivity : activitySetRepo){
            LocalDate activityEndDate = null;
            LocalDate activityStartDate = new java.sql.Date (userActivity.getC_Activity().getDateStart().getTime()).toLocalDate();
            if(userActivity.getC_Activity().getDateEnd() != null)
                activityEndDate = new java.sql.Date (userActivity.getC_Activity().getDateEnd().getTime()).toLocalDate();
            if(
                    (activityStartDate.getYear() > year || (activityStartDate.getYear() == year && activityStartDate.getMonthValue() > month)) ||
                            (activityEndDate != null && (activityEndDate.getYear() < year || (activityEndDate.getYear() == year && activityEndDate.getMonthValue() < month)))
            )
                    activitySetRepo.remove(userActivity);
        }
        model.addAttribute("userActivityList", userRepo.getActivities());

        for(CompiledProjectActivity compiledProjectActivity : compiledProjectActivityList){
            ProjectActivity projectActivityRepo = projectActivityService.findActivityByActivityKeyAndProject(compiledProjectActivity.getActivityKey(), compiledProjectActivity.getProject());
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

        List<CompiledStandardActivity> compiledStandardActivityList = compiledStandardActivityService
                .findActivitiesByUsernameAndMonthAndYear(username, month, year);
        HashSet<CompiledStandardActivityRow> standardActivityHashSet = new HashSet<>();
        model.addAttribute("allStandardList", standardActivityService.findAll());
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
        model.addAttribute("standardActivityList", standardActivityHashSet);

        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "tracking";
    }

    @PostMapping ("/tracking/{year}/{month}")
    public ResponseEntity<String> trackingMonthYear(@PathVariable int year,
                                                    @PathVariable int month,
                                                    @RequestParam String projectActivityKeys,
                                                    @RequestParam String locationName,
                                                    Principal principal) throws ParseException {
        String username = principal.getName();
        LocalDate passedDate = LocalDate.of(year, month, 1);
        User userRepo = userService.findUserByUsername(principal.getName());
        String userTime = userRepo.getTime();
        Pattern actPattern = Pattern.compile("Std:.*");
        Matcher matcher = actPattern.matcher(projectActivityKeys);

        if(LocalDate.now().isAfter(passedDate.plusMonths(1).plusDays(6))){
            return AdviceController.responseBadRequest("Cannot edit this timesheet. Last editable day was "+ passedDate.plusMonths(1).plusDays(6));
        }

        if(matcher.find()){
            String[] list = projectActivityKeys.split(":");
            String activityKey = list[1];
            StandardActivity standardActivityRepo = standardActivityService.findStandardActivityByActivityKey(activityKey);
            List<CompiledStandardActivity> compiledStandardActivityList = compiledStandardActivityService
                    .findCompiledStandardActivitiesListByUsernameAndLocationAndActivityKeyNameAndMonthAndYear(username, locationName, activityKey, month, year);
            if(!compiledStandardActivityList.isEmpty())
                return AdviceController.responseBadRequest("Cannot add '" + projectActivityKeys + "' for location '" + locationName +"' in time sheet. (Already added)");
            for(int i=1; i <= passedDate.lengthOfMonth(); i++){
                String dateString = "" + i + "-" + month + "-" + year;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date dailyDate = dateFormat.parse(dateString);
                CompiledStandardActivity csa = CompiledStandardActivity.builder()
                        .activityKey(standardActivityRepo.getActivityKey())
                        .c_Activity(standardActivityRepo)
                        .username(username)
                        .c_Username(userRepo)
                        .locationName(locationName)
                        .c_Location(locationService.findLocationByLocationName(locationName))
                        .date(dailyDate)
                        .hours(0)
                        .build();
                compiledStandardActivityService.saveCompiledStandardActivity(csa);
            }
        }
        else{
            String[] list = projectActivityKeys.split(":");
            ProjectActivity projectActivityRepo = projectActivityService.findActivityByActivityKeyAndProject(list[1], list[0]);
            String projectKey = list[0];
            String activityKey = list[1];
            List<CompiledProjectActivity> compiledProjectActivityList = compiledProjectActivityService
                    .findCompiledProjectActivitiesListByUsernameAndLocationNameAndProjectAndActivityKeyAndMonthAndYear(username, locationName, projectKey, activityKey, month, year);
            if(!compiledProjectActivityList.isEmpty())
                return AdviceController.responseBadRequest("Cannot add '" + projectActivityKeys + "' for location '" + locationName +"' in time sheet. (Already added)");

            LocalDate activityEndDate = null;
            LocalDate activityStartDate = new java.sql.Date (projectActivityRepo.getDateStart().getTime()).toLocalDate();
            if(projectActivityRepo.getDateEnd() != null)
                activityEndDate = new java.sql.Date (projectActivityRepo.getDateEnd().getTime()).toLocalDate();
            if(
                    (activityStartDate.getYear() > year || (activityStartDate.getYear() == year && activityStartDate.getMonthValue() > month)) ||
                    (activityEndDate != null && (activityEndDate.getYear() < year || (activityEndDate.getYear() == year && activityEndDate.getMonthValue() < month)))
            )
                return AdviceController.responseBadRequest("Cannot add '" + projectActivityKeys + "' for location '" + locationName +"' in time sheet. (Activity period was" + projectActivityRepo.getDateStart() + " - " + projectActivityRepo.getDateEnd() +")");


            for(int i=1; i <= passedDate.lengthOfMonth(); i++){
                String dateString = "" + i + "-" + month + "-" + year;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Date dailyDate = dateFormat.parse(dateString);
                LocalDate cycleLocalDate = LocalDate.of(year, month, i);
                int pos = -1;
                switch(cycleLocalDate.getDayOfWeek()){
                    case MONDAY:
                        pos = 0;
                        break;
                    case TUESDAY:
                        pos = 1;
                        break;
                    case WEDNESDAY:
                        pos = 2;
                        break;
                    case THURSDAY:
                        pos = 3;
                        break;
                    case FRIDAY:
                        pos = 4;
                        break;
                }
                int hours = 0;
                if(pos != -1)
                    hours = Character.getNumericValue(userTime.charAt(pos));
                CompiledProjectActivity cpa = CompiledProjectActivity.builder()
                        .project(projectActivityRepo.getProject())
                        .activityKey(projectActivityRepo.getActivityKey())
                        .c_Activity(projectActivityRepo)
                        .username(username)
                        .c_Username(userRepo)
                        .locationName(locationName)
                        .c_Location(locationService.findLocationByLocationName(locationName))
                        .date(dailyDate)
                        .hours(hours)
                        .build();
                compiledProjectActivityService.saveCompiledProjectActivity(cpa);
            }
        }
        return AdviceController.responseOk("Activity added to time sheet successfully.");
    }
}
