package eu.rationence.pat.controller;
import eu.rationence.pat.model.*;
import eu.rationence.pat.service.*;

import eu.rationence.pat.model.dto.CompiledProjectActivityRow;
import eu.rationence.pat.model.dto.CompiledStandardActivityRow;
import eu.rationence.pat.model.dto.CompiledUserProjectActivityRow;
import eu.rationence.pat.model.dto.CompiledUserStandardActivityRow;

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
public class TrackingController {
    private final UserService userService;
    private final CompiledProjectActivityService compiledProjectActivityService;
    private final CompiledStandardActivityService compiledStandardActivityService;
    private final ProjectActivityService projectActivityService;
    private final StandardActivityService standardActivityService;
    private final LocationService locationService;
    private final MonthlyNoteService monthlyNoteService;

    private static final String ERROR_PAGE = "error";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String DATE_FORMAT_DDMMYYYY = "dd-MM-yyyy";
    private static final String CANNOT_MODIFY_TIME_SHEET_STRING = "Cannot edit this timesheet. Last editable day was ";
    private static final String CANNOT_MODIFY_TIME_SHEET_LOCKED_STRING = "Cannot edit this timesheet. It is locked.";

    final int[][] festivityListDayMonth = {{1,1}, {6,1}, {25,4}, {1,5}, {2,6}, {15,8}, {16, 8}, {1,11}, {8,12}, {25,12}, {26,12}, {0, 0}};

    @Autowired
    public TrackingController(UserService userService, CompiledProjectActivityService compiledProjectActivityService, CompiledStandardActivityService compiledStandardActivityService, ProjectActivityService projectActivityService, StandardActivityService standardActivityService, LocationService locationService, MonthlyNoteService monthlyNoteService) {
        this.userService = userService;
        this.compiledProjectActivityService = compiledProjectActivityService;
        this.compiledStandardActivityService = compiledStandardActivityService;
        this.projectActivityService = projectActivityService;
        this.standardActivityService = standardActivityService;
        this.locationService = locationService;
        this.monthlyNoteService = monthlyNoteService;
    }

    @GetMapping ("/report")
    public String report(Model model, Principal principal) throws ParseException {
        LocalDate currentDate = LocalDate.now();
        return reportMonthYear(currentDate.getYear(), currentDate.getMonthValue(), model, principal);
    }

    @GetMapping ("/report/{year}/{month}")
    public String reportMonthYear(@PathVariable int year,
                                  @PathVariable int month,
                                  Model model, Principal principal) throws ParseException {
        User userLogged = userService.find(principal.getName());
        if(userLogged == null)
            return ERROR_PAGE;

        LocalDate passedDate = LocalDate.of(year, month, 1);
        if(passedDate.isAfter(LocalDate.now()))
            return ERROR_PAGE;

        LocalDate easterMonday = getEasterDate(year).plusDays(1);
        festivityListDayMonth[festivityListDayMonth.length - 1][0] = easterMonday.getDayOfMonth();
        festivityListDayMonth[festivityListDayMonth.length - 1][1] = easterMonday.getMonthValue();

        List<User> uncompiledTimeSheetUserList = userService.findAll();
        Iterator<User> iter = uncompiledTimeSheetUserList.iterator();
        while(iter.hasNext()){
            User user = iter.next();
            MonthlyNote monthlyNote = monthlyNoteService.find(user.getUsername(), new SimpleDateFormat(DATE_FORMAT_DDMMYYYY).parse("1-" + month + "-" + year));
            if(!user.isEnabled() || (monthlyNote != null && monthlyNote.isLocked()))
                iter.remove();
        }
        model.addAttribute("uncompiledTimeSheetUsers", uncompiledTimeSheetUserList);
        model.addAttribute("locationList", locationService.findAll());

        boolean lastCompilableSheet = month == LocalDate.now().getMonthValue() && year == LocalDate.now().getYear();
        model.addAttribute("nextAvailable", lastCompilableSheet);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("monthDays", passedDate.lengthOfMonth());
        model.addAttribute("monthName", passedDate.getMonth());

        Set<Integer> weekendAndFestivityDaySet = new HashSet<>();

        for(int i=1; i <= passedDate.lengthOfMonth(); i++){
            LocalDate cycleLocalDate = LocalDate.of(year, month, i);
            int pos = cycleLocalDate.getDayOfWeek().getValue()-1;
            if(cycleLocalDate.getDayOfWeek() == DayOfWeek.SATURDAY || cycleLocalDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekendAndFestivityDaySet.add(i);
                pos = -1;
            }
            int hours = 0;
            for (int[] ints : festivityListDayMonth) {
                if (ints[1] == month && ints[0] == i) {
                    pos = -1;
                    break;
                }
            }
            if(pos != -1)
                hours = Character.getNumericValue(userService.find(principal.getName()).getTime().charAt(pos));
            model.addAttribute("hoursToWork."+i, hours);
        }
        for (int[] ints : festivityListDayMonth) {
            if (ints[1] == month) {
                weekendAndFestivityDaySet.add(ints[0]);
            }
        }
        model.addAttribute("weekendDays", weekendAndFestivityDaySet);

        int previousMonth = month-1;
        int previousYear = year;
        int nextMonth = month + 1;
        int nextYear = year;
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

        List<CompiledProjectActivity> compiledProjectActivityList = compiledProjectActivityService
                .find(month, year);
        HashSet<CompiledUserProjectActivityRow> projectActivityHashSet = new HashSet<>();
        Date compiledDate = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY).parse("1-" + month + "-" + year);
        for(CompiledProjectActivity compiledProjectActivity : compiledProjectActivityList){
            MonthlyNote monthlyNote = monthlyNoteService.find(compiledProjectActivity.getUsername(), compiledDate);
            if(monthlyNote.isLocked()) {
                projectActivityHashSet.add(CompiledUserProjectActivityRow.builder()
                        .username(compiledProjectActivity.getUsername())
                        .project(compiledProjectActivity.getProject())
                        .projectDesc(compiledProjectActivity.getC_Activity().getC_Project().getProjectDesc())
                        .activityKey(compiledProjectActivity.getActivityKey())
                        .location(compiledProjectActivity.getLocationName()).build());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(compiledProjectActivity.getDate());
                model.addAttribute(compiledProjectActivity.getProject() + "."
                        + compiledProjectActivity.getActivityKey() + "."
                        + compiledProjectActivity.getUsername() + "."
                        + compiledProjectActivity.getLocationName() + "."
                        + calendar.get(Calendar.DAY_OF_MONTH), compiledProjectActivity.getHours());
            }
        }
        model.addAttribute("projectActivityList", projectActivityHashSet);

        List<CompiledStandardActivity> compiledStandardActivityList = compiledStandardActivityService.find(month, year);
        HashSet<CompiledUserStandardActivityRow> standardActivityHashSet = new HashSet<>();
        model.addAttribute("allStandardList", standardActivityService.findAll());

        for(CompiledStandardActivity compiledStandardActivity : compiledStandardActivityList){
            MonthlyNote monthlyNote = monthlyNoteService.find(compiledStandardActivity.getUsername(), compiledDate);
            if(monthlyNote.isLocked()) {
                standardActivityHashSet.add(CompiledUserStandardActivityRow.builder()
                        .username(compiledStandardActivity.getUsername())
                        .activityKey(compiledStandardActivity.getActivityKey())
                        .location(compiledStandardActivity.getLocationName()).build());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(compiledStandardActivity.getDate());
                model.addAttribute("Standard."
                        + compiledStandardActivity.getActivityKey() + "."
                        + compiledStandardActivity.getUsername() + "."
                        + compiledStandardActivity.getLocationName() + "."
                        + calendar.get(Calendar.DAY_OF_MONTH), compiledStandardActivity.getHours());
            }
        }
        model.addAttribute("standardActivityList", standardActivityHashSet);
        model.addAttribute("userTeam", userLogged.getTeam().getTeamName());
        model.addAttribute("userTeamName", userLogged.getTeam().getTeamDesc());
        return "report";
    }


    @GetMapping ("/tracking")
    public String tracking(Model model, Principal principal) throws ParseException {
        LocalDate currentDate = LocalDate.now();
        return getTrackingMonthYear(currentDate.getYear(), currentDate.getMonthValue(), model, principal);
    }

    @GetMapping("/tracking/{username}")
    public String trackingUsername(@PathVariable String username,
                                   Model model,
                                   Principal principal) throws ParseException {
        User userAdmin = userService.find(principal.getName());
        User userTimeSheet = userService.find(username);
        if((!userAdmin.getRole().getRoleName().equals(ADMIN_ROLE) && !principal.getName().equals(username)) || userTimeSheet == null)
            return ERROR_PAGE;
        LocalDate currentDate = LocalDate.now();
        return getTrackingMonthYear(currentDate.getYear(), currentDate.getMonthValue(), username, model, principal);
    }

    @GetMapping ("/tracking/{year}/{month}")
    public String getTrackingMonthYear(@PathVariable int year,
                                       @PathVariable int month,
                                       Model model, Principal principal) throws ParseException {
        return getTrackingMonthYear(year, month, principal.getName(), model, principal);
    }

    @GetMapping ("/tracking/{year}/{month}/{username}")
    public String getTrackingMonthYear(@PathVariable int year,
                                       @PathVariable int month,
                                       @PathVariable String username,
                                       Model model, Principal principal) throws ParseException {
        User userAdmin = userService.find(principal.getName());
        if(!userAdmin.getRole().getRoleName().equals(ADMIN_ROLE) && !principal.getName().equals(username))
            return ERROR_PAGE;

        LocalDate passedDate = LocalDate.of(year, month, 1);
        if(passedDate.isAfter(LocalDate.now()))
            return ERROR_PAGE;

        User userLogged = userService.find(principal.getName());
        if(userLogged == null)
            return ERROR_PAGE;

        User userRepo = userService.find(username);
        if(userRepo == null)
            return ERROR_PAGE;

        model.addAttribute("locationList", locationService.findAll());

        LocalDate easterMonday = getEasterDate(year).plusDays(1);
        festivityListDayMonth[festivityListDayMonth.length-1][0] = easterMonday.getDayOfMonth();
        festivityListDayMonth[festivityListDayMonth.length-1][1] = easterMonday.getMonthValue();

        model.addAttribute("userTimeSheetDescription", userService.find(username).getDescription());
        model.addAttribute("userTimeSheetUsername", username);

        boolean lastCompilableSheet = month == LocalDate.now().getMonthValue() && year == LocalDate.now().getYear();
        model.addAttribute("nextAvailable", lastCompilableSheet);
        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("monthDays", passedDate.lengthOfMonth());
        model.addAttribute("monthName", passedDate.getMonth());

        Set<Integer> weekendAndFestivityDaySet = new HashSet<>();

        for(int i=1; i <= passedDate.lengthOfMonth(); i++){
            LocalDate cycleLocalDate = LocalDate.of(year, month, i);
            int pos = cycleLocalDate.getDayOfWeek().getValue()-1;
            if(cycleLocalDate.getDayOfWeek() == DayOfWeek.SATURDAY || cycleLocalDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
                weekendAndFestivityDaySet.add(i);
                pos = -1;
            }
            int hours = 0;
            for (int[] ints : festivityListDayMonth) {
                if (ints[1] == month && ints[0] == i) {
                    pos = -1;
                    break;
                }
            }
            if(pos != -1)
                hours = Character.getNumericValue(userRepo.getTime().charAt(pos));
            model.addAttribute("hoursToWork."+i, hours);
        }
        for (int[] ints : festivityListDayMonth) {
            if (ints[1] == month) {
                weekendAndFestivityDaySet.add(ints[0]);
            }
        }
        model.addAttribute("weekendDays", weekendAndFestivityDaySet);

        int previousMonth = month-1;
        int previousYear = year;
        int nextMonth = month + 1;
        int nextYear = year;
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

        Date dateMonthlyNote = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY).parse("1-" + month + "-" + year);
        MonthlyNote monthlyNote = monthlyNoteService.find(username, dateMonthlyNote);
        if(monthlyNote != null) {
            model.addAttribute("monthlyNote", monthlyNote.getNote());
            model.addAttribute("trackingLocked", monthlyNote.isLocked());
        }

        List<CompiledProjectActivity> compiledProjectActivityList = compiledProjectActivityService
                .find(username, month, year);
        HashSet<CompiledProjectActivityRow> projectActivityHashSet = new HashSet<>();

        Set<UserActivity> activitySetRepo = userRepo.getActivities();
        Iterator<UserActivity> iter = activitySetRepo.iterator();
        while(iter.hasNext()){
            UserActivity userActivity = iter.next();
            LocalDate activityEndDate = null;
            LocalDate activityStartDate = new java.sql.Date (userActivity.getC_Activity().getDateStart().getTime()).toLocalDate();
            if(userActivity.getC_Activity().getDateEnd() != null)
                activityEndDate = new java.sql.Date (userActivity.getC_Activity().getDateEnd().getTime()).toLocalDate();
            if((activityStartDate.getYear() > year || (activityStartDate.getYear() == year && activityStartDate.getMonthValue() > month)) ||
                    (activityEndDate != null && (activityEndDate.getYear() < year || (activityEndDate.getYear() == year && activityEndDate.getMonthValue() < month))))
                iter.remove();
        }
        model.addAttribute("userActivityList", activitySetRepo);

        for(CompiledProjectActivity compiledProjectActivity : compiledProjectActivityList){
            projectActivityHashSet.add(CompiledProjectActivityRow.builder()
                    .project(compiledProjectActivity.getProject())
                    .projectDesc(compiledProjectActivity.getC_Activity().getC_Project().getProjectDesc())
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
                .find(username, month, year);
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
        model.addAttribute("userTeam", userLogged.getTeam().getTeamName());
        model.addAttribute("userTeamName", userLogged.getTeam().getTeamDesc());
        return "tracking";
    }

    @PostMapping ("/tracking/{year}/{month}/{username}")
    public ResponseEntity<String> addTrackingMonthYear(@PathVariable int year,
                                                       @PathVariable int month,
                                                       @PathVariable String username,
                                                       @RequestParam String projectActivityKeys,
                                                       @RequestParam String locationName,
                                                       @RequestParam (required = false) boolean autocompile,
                                                       Principal principal) throws ParseException {

        User userRepo = userService.find(principal.getName());
        if(!userRepo.getRole().getRoleName().equals(ADMIN_ROLE) && !principal.getName().equals(username))
            return AdviceController.responseForbidden("Forbidden action for user " + userRepo.getUsername());

        Date date = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY).parse("1-" + month + "-" + year);
        MonthlyNote monthlyNote = monthlyNoteService.find(username, date);
        if(monthlyNote != null && monthlyNote.isLocked())
            return AdviceController.responseForbidden(CANNOT_MODIFY_TIME_SHEET_LOCKED_STRING);

        LocalDate passedDate = LocalDate.of(year, month, 1);
        userRepo = userService.find(username);

        if(LocalDate.now().isAfter(passedDate.plusMonths(1).plusDays(25))){
            return AdviceController.responseBadRequest(CANNOT_MODIFY_TIME_SHEET_STRING + passedDate.plusMonths(1).plusDays(25));
        }

        if(monthlyNote == null){
            MonthlyNote newMonthlyNote = MonthlyNote.builder().c_Username(userRepo).username(username).date(date).note("").locked(false).build();
            monthlyNoteService.save(newMonthlyNote);
        }

        String userTime = userRepo.getTime();
        Pattern actPattern = Pattern.compile("(..*):(..*)");
        Matcher matcher = actPattern.matcher(projectActivityKeys);
        boolean matchFound = matcher.find();


        LocalDate easterMonday = getEasterDate(year).plusDays(1);
        festivityListDayMonth[festivityListDayMonth.length - 1][0] = easterMonday.getDayOfMonth();
        festivityListDayMonth[festivityListDayMonth.length - 1][1] = easterMonday.getMonthValue();

        if(matchFound) {
            if(matcher.group(1).equals("Std")) {
                String activityKey = matcher.group(2);
                StandardActivity standardActivityRepo = standardActivityService.findStandardActivityByActivityKey(activityKey);
                if (standardActivityRepo == null)
                    return AdviceController.responseNotFound("Cannot add " + activityKey + " to time sheet. (Not found)");
                List<CompiledStandardActivity> compiledStandardActivityList = compiledStandardActivityService
                        .find(username, locationName, activityKey, month, year);
                if (!compiledStandardActivityList.isEmpty())
                    return AdviceController.responseBadRequest("Cannot add '" + projectActivityKeys + "' for location '" + locationName + "' in time sheet. (Already added)");
                for (int day = 1; day <= passedDate.lengthOfMonth(); day++) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY);
                    Date dailyDate = dateFormat.parse("" + day + "-" + month + "-" + year);
                    LocalDate cycleLocalDate = LocalDate.of(year, month, day);
                    int hours = 0;
                    if (cycleLocalDate.getDayOfWeek().getValue() <= 5 && autocompile)
                        hours = Character.getNumericValue(userTime.charAt(cycleLocalDate.getDayOfWeek().getValue()-1));
                    for (int[] ints : festivityListDayMonth) {
                        if (ints[1] == month && ints[0] == day) {
                            hours = 0;
                            break;
                        }
                    }
                    CompiledStandardActivity csa = CompiledStandardActivity.builder()
                            .activityKey(standardActivityRepo.getActivityKey())
                            .c_Activity(standardActivityRepo)
                            .username(username)
                            .c_Username(userRepo)
                            .locationName(locationName)
                            .c_Location(locationService.find(locationName))
                            .date(dailyDate)
                            .hours(hours)
                            .build();
                    compiledStandardActivityService.save(csa);
                }
                return AdviceController.responseOk("Activity added to time sheet successfully.");
            }
            else {
                String projectKey = matcher.group(1);
                String activityKey = matcher.group(2);
                ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectKey);
                if (projectActivityRepo == null)
                    return AdviceController.responseNotFound("Cannot add " + projectKey + ":" + activityKey + " to time sheet. (Not found)");
                List<CompiledProjectActivity> compiledProjectActivityList = compiledProjectActivityService
                        .find(username, locationName, projectKey, activityKey, month, year);
                if (!compiledProjectActivityList.isEmpty())
                    return AdviceController.responseBadRequest("Cannot add '" + projectActivityKeys + "' for location '" + locationName + "' in time sheet. (Already added)");

                LocalDate activityEndDate = null;
                LocalDate activityStartDate = new java.sql.Date(projectActivityRepo.getDateStart().getTime()).toLocalDate();
                if (projectActivityRepo.getDateEnd() != null)
                    activityEndDate = new java.sql.Date(projectActivityRepo.getDateEnd().getTime()).toLocalDate();
                if (
                        (activityStartDate.getYear() > year || (activityStartDate.getYear() == year && activityStartDate.getMonthValue() > month)) ||
                                (activityEndDate != null && (activityEndDate.getYear() < year || (activityEndDate.getYear() == year && activityEndDate.getMonthValue() < month)))
                )
                    return AdviceController.responseBadRequest("Cannot add '" + projectActivityKeys + "' for location '" + locationName + "' in time sheet. (Activity period was" + projectActivityRepo.getDateStart() + " - " + projectActivityRepo.getDateEnd() + ")");


                for (int day = 1; day <= passedDate.lengthOfMonth(); day++) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY);
                    Date dailyDate = dateFormat.parse("" + day + "-" + month + "-" + year);
                    LocalDate cycleLocalDate = LocalDate.of(year, month, day);
                    int hours = 0;
                    if (cycleLocalDate.getDayOfWeek().getValue() <= 5 && autocompile)
                        hours = Character.getNumericValue(userTime.charAt(cycleLocalDate.getDayOfWeek().getValue()-1));
                    for (int[] ints : festivityListDayMonth) {
                        if (ints[1] == month && ints[0] == day) {
                            hours = 0;
                            break;
                        }
                    }
                    CompiledProjectActivity cpa = CompiledProjectActivity.builder()
                            .project(projectActivityRepo.getProject())
                            .activityKey(projectActivityRepo.getActivityKey())
                            .c_Activity(projectActivityRepo)
                            .username(username)
                            .c_Username(userRepo)
                            .locationName(locationName)
                            .c_Location(locationService.find(locationName))
                            .date(dailyDate)
                            .hours(hours)
                            .build();
                    compiledProjectActivityService.save(cpa);
                }
                return AdviceController.responseOk("Activity added to time sheet successfully.");
            }
        }
        return AdviceController.responseBadRequest("Activities cannot be added. Bad request.");
    }

    @PutMapping ("/tracking/{year}/{month}/{username}")
    public ResponseEntity<String> updateNoteMonthYear(@PathVariable int year,
                                                      @PathVariable int month,
                                                      @PathVariable String username,
                                                      @RequestParam String projectActivityKeys,
                                                      @RequestParam String locationName,
                                                      @RequestParam int day,
                                                      @RequestParam int hour,
                                                      Principal principal) throws ParseException {

        User userRepo = userService.find(principal.getName());
        if(!userRepo.getRole().getRoleName().equals(ADMIN_ROLE) && !principal.getName().equals(username))
            return AdviceController.responseForbidden("Forbidden action for user " + userRepo.getUsername());

        Date date = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY).parse("1-" + month + "-" + year);
        MonthlyNote monthlyNote = monthlyNoteService.find(username, date);
        if(monthlyNote != null && monthlyNote.isLocked())
            return AdviceController.responseForbidden(CANNOT_MODIFY_TIME_SHEET_LOCKED_STRING);

        LocalDate passedDate = LocalDate.of(year, month, 1);
        userRepo = userService.find(username);
        if(userRepo == null)
            return AdviceController.responseNotFound("Cannot update " + username + " time sheet. (Not found)");

        Pattern actPattern = Pattern.compile("(..*):(..*)");
        Matcher matcher = actPattern.matcher(projectActivityKeys);
        boolean matchFound = matcher.find();

        if(LocalDate.now().isAfter(passedDate.plusMonths(1).plusDays(25))){
            return AdviceController.responseBadRequest(CANNOT_MODIFY_TIME_SHEET_STRING+ passedDate.plusMonths(1).plusDays(25));
        }

        if(matchFound) {
            if (matcher.group(1).equals("Std")) {
                String activityKey = matcher.group(2);
                StandardActivity standardActivityRepo = standardActivityService.findStandardActivityByActivityKey(activityKey);
                if (standardActivityRepo == null)
                    return AdviceController.responseNotFound("Cannot update " + activityKey + " to time sheet. (Not found)");
                List<CompiledStandardActivity> compiledStandardActivityList = compiledStandardActivityService
                        .find(username, locationName, activityKey, month, year);
                if (compiledStandardActivityList.isEmpty())
                    return AdviceController.responseNotFound("Cannot update '" + projectActivityKeys + "' for location '" + locationName + "' in time sheet. (Not found)");

                String dateString = "" + day + "-" + month + "-" + year;
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY);
                Date dailyDate = dateFormat.parse(dateString);
                CompiledStandardActivity csa = CompiledStandardActivity.builder()
                        .activityKey(standardActivityRepo.getActivityKey())
                        .c_Activity(standardActivityRepo)
                        .username(username)
                        .c_Username(userRepo)
                        .locationName(locationName)
                        .c_Location(locationService.find(locationName))
                        .date(dailyDate)
                        .hours(hour)
                        .build();
                compiledStandardActivityService.save(csa);
                return AdviceController.responseOk("Time sheet activities updated successfully.");
            }
            else{
                String projectKey = matcher.group(1);
                String activityKey = matcher.group(2);
                ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectKey);
                if(projectActivityRepo == null)
                    return AdviceController.responseNotFound("Cannot update " + projectKey + ":" + activityKey + " in time sheet. (Not found)");
                List<CompiledProjectActivity> compiledProjectActivityList = compiledProjectActivityService
                        .find(username, locationName, projectKey, activityKey, month, year);
                if(compiledProjectActivityList.isEmpty())
                    return AdviceController.responseNotFound("Cannot update '" + projectActivityKeys + "' for location '" + locationName +"' in time sheet. (Not found)");

                LocalDate activityEndDate = null;
                LocalDate activityStartDate = new java.sql.Date (projectActivityRepo.getDateStart().getTime()).toLocalDate();
                if(projectActivityRepo.getDateEnd() != null)
                    activityEndDate = new java.sql.Date (projectActivityRepo.getDateEnd().getTime()).toLocalDate();
                if(
                        (activityStartDate.getYear() > year || (activityStartDate.getYear() == year && activityStartDate.getMonthValue() > month)) ||
                                (activityEndDate != null && (activityEndDate.getYear() < year || (activityEndDate.getYear() == year && activityEndDate.getMonthValue() < month)))
                )
                    return AdviceController.responseBadRequest("Cannot update '" + projectActivityKeys + "' for location '" + locationName +"' in time sheet. (Activity period was" + projectActivityRepo.getDateStart() + " - " + projectActivityRepo.getDateEnd() +")");

                String dateString = "" + day + "-" + month + "-" + year;
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY);
                Date dailyDate = dateFormat.parse(dateString);
                CompiledProjectActivity cpa = CompiledProjectActivity.builder()
                        .project(projectActivityRepo.getProject())
                        .activityKey(projectActivityRepo.getActivityKey())
                        .c_Activity(projectActivityRepo)
                        .username(username)
                        .c_Username(userRepo)
                        .locationName(locationName)
                        .c_Location(locationService.find(locationName))
                        .date(dailyDate)
                        .hours(hour)
                        .build();
                compiledProjectActivityService.save(cpa);
                return AdviceController.responseOk("Time sheet activities updated successfully.");
            }
        }
        return AdviceController.responseBadRequest("Activities cannot be updated. Bad request.");
    }

    @DeleteMapping ("/tracking/{year}/{month}/{username}")
    public ResponseEntity<String> deleteTrackingMonthYear(@PathVariable int year,
                                                          @PathVariable int month,
                                                          @PathVariable String username,
                                                          @RequestParam String projectActivityKeys,
                                                          @RequestParam String locationName,
                                                          Principal principal) throws ParseException {

        User userRepo = userService.find(principal.getName());
        if(!userRepo.getRole().getRoleName().equals(ADMIN_ROLE) && !principal.getName().equals(username))
            return AdviceController.responseForbidden("Forbidden action for user " + userRepo.getUsername());

        Date date = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY).parse("1-" + month + "-" + year);
        MonthlyNote monthlyNote = monthlyNoteService.find(username, date);
        if(monthlyNote != null && monthlyNote.isLocked())
            return AdviceController.responseForbidden(CANNOT_MODIFY_TIME_SHEET_LOCKED_STRING);

        LocalDate passedDate = LocalDate.of(year, month, 1);
        userRepo = userService.find(username);
        if(userRepo == null)
            return AdviceController.responseNotFound("Cannot update " + username + " time sheet. (Not found)");

        Pattern actPattern = Pattern.compile("(..*):(..*)");
        Matcher matcher = actPattern.matcher(projectActivityKeys);
        boolean matchFound = matcher.find();

        if(LocalDate.now().isAfter(passedDate.plusMonths(1).plusDays(25))){
            return AdviceController.responseBadRequest(CANNOT_MODIFY_TIME_SHEET_STRING+ passedDate.plusMonths(1).plusDays(25));
        }
        if(matchFound) {
            if(matcher.group(1).equals("Std")) {
                String activityKey = matcher.group(2);
                StandardActivity standardActivityRepo = standardActivityService.findStandardActivityByActivityKey(activityKey);
                if (standardActivityRepo == null)
                    return AdviceController.responseNotFound("Cannot delete " + activityKey + " from time sheet. (Not found)");
                List<CompiledStandardActivity> compiledStandardActivityList = compiledStandardActivityService
                        .find(username, locationName, activityKey, month, year);
                if (compiledStandardActivityList.isEmpty())
                    return AdviceController.responseNotFound("Cannot delete '" + projectActivityKeys + "' for location '" + locationName + "' in time sheet. (Not found)");
                for (int i = 1; i <= passedDate.lengthOfMonth(); i++) {
                    String dateString = "" + i + "-" + month + "-" + year;
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY);
                    Date dailyDate = dateFormat.parse(dateString);
                    compiledStandardActivityService.delete(activityKey, username, locationName, dailyDate);
                }
                return AdviceController.responseOk("Activity " + projectActivityKeys + " deleted from time sheet successfully.");
            }
            else{
                String projectKey = matcher.group(1);
                String activityKey = matcher.group(2);
                ProjectActivity projectActivityRepo = projectActivityService.find(activityKey, projectKey);
                if(projectActivityRepo == null)
                    return AdviceController.responseNotFound("Cannot delete " + projectKey + ":" + activityKey + " from time sheet. (Not found)");
                List<CompiledProjectActivity> compiledProjectActivityList = compiledProjectActivityService
                        .find(username, locationName, projectKey, activityKey, month, year);
                if(compiledProjectActivityList.isEmpty())
                    return AdviceController.responseNotFound("Cannot delete '" + projectActivityKeys + "' for location '" + locationName +"' in time sheet. (Not found)");

                LocalDate activityEndDate = null;
                LocalDate activityStartDate = new java.sql.Date (projectActivityRepo.getDateStart().getTime()).toLocalDate();
                if(projectActivityRepo.getDateEnd() != null)
                    activityEndDate = new java.sql.Date (projectActivityRepo.getDateEnd().getTime()).toLocalDate();
                if(
                        (activityStartDate.getYear() > year || (activityStartDate.getYear() == year && activityStartDate.getMonthValue() > month)) ||
                                (activityEndDate != null && (activityEndDate.getYear() < year || (activityEndDate.getYear() == year && activityEndDate.getMonthValue() < month)))
                )
                    return AdviceController.responseBadRequest("Cannot delete '" + projectActivityKeys + "' for location '" + locationName +"' in time sheet. (Activity period was" + projectActivityRepo.getDateStart() + " - " + projectActivityRepo.getDateEnd() +")");


                for(int dayNum=1; dayNum <= passedDate.lengthOfMonth(); dayNum++){
                    String dateString = "" + dayNum + "-" + month + "-" + year;
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY);
                    Date dailyDate = dateFormat.parse(dateString);
                    compiledProjectActivityService.delete(activityKey, projectKey, username, locationName, dailyDate);
                }
                return AdviceController.responseOk("Activity " + projectActivityKeys + " deleted from time sheet successfully.");
            }
        }
        return AdviceController.responseBadRequest("Activities cannot be deleted. Bad request.");
    }

    @PutMapping ("/trackingNote/{year}/{month}/{username}")
    public ResponseEntity<String> updateNoteMonthYear(@PathVariable int year,
                                                      @PathVariable int month,
                                                      @PathVariable String username,
                                                      @RequestParam String note,
                                                      Principal principal) throws ParseException {

        User userRepo = userService.find(principal.getName());
        if(!userRepo.getRole().getRoleName().equals(ADMIN_ROLE) && !principal.getName().equals(username))
            return AdviceController.responseForbidden("Forbidden action for user " + userRepo.getUsername());

        LocalDate passedDate = LocalDate.of(year, month, 1);
        userRepo = userService.find(username);
        if(userRepo == null)
            return AdviceController.responseNotFound("Cannot update " + username + " time sheet. (Not found)");
        Date date = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY).parse("1-" + month + "-" + year);

        if(LocalDate.now().isAfter(passedDate.plusMonths(1).plusDays(25))){
            return AdviceController.responseForbidden("Cannot edit this timesheet note. Last editable day was "+ passedDate.plusMonths(1).plusDays(25));
        }
        MonthlyNote monthlyNote = monthlyNoteService.find(username, date);
        if(monthlyNote != null && monthlyNote.isLocked())
            return AdviceController.responseForbidden("Cannot edit this timesheet note. Time sheet is locked.");

        if(monthlyNote == null){
            MonthlyNote monthlyNoteNew = MonthlyNote.builder()
                    .c_Username(userRepo).username(username).date(date).note(note).locked(false).build();
            monthlyNoteService.save(monthlyNoteNew);
            return AdviceController.responseCreated("Note created.");
        }
        else{
            monthlyNote.setNote(note);
            monthlyNoteService.save(monthlyNote);
            return AdviceController.responseOk("Note saved.");
        }
    }

    @PutMapping("/lockTracking/{year}/{month}/{username}")
    public ResponseEntity<String> lockTrackingYearMonth(@PathVariable int year,
                                                        @PathVariable int month,
                                                        @PathVariable String username,
                                                        Principal principal) throws ParseException {

        User userRepo = userService.find(principal.getName());
        if(!userRepo.getRole().getRoleName().equals(ADMIN_ROLE) && !principal.getName().equals(username))
            return AdviceController.responseForbidden("Forbidden action for user " + userRepo.getUsername());

        LocalDate passedDate = LocalDate.of(year, month, 1);
        Date date = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY).parse("1-" + month + "-" + year);
        MonthlyNote monthlyNote = monthlyNoteService.find(username, date);
        userRepo = userService.find(username);
        if(userRepo == null)
            return AdviceController.responseNotFound("Cannot update " + username + " time sheet. (Not found)");
        else if(LocalDate.now().isAfter(passedDate.plusMonths(1).plusDays(25)))
            return AdviceController.responseForbidden(CANNOT_MODIFY_TIME_SHEET_STRING+ passedDate.plusMonths(1).plusDays(25));
        else if(monthlyNote == null)
            return AdviceController.responseNotFound("Cannot submit time sheet. (Not found)");
        else if(monthlyNote.isLocked())
            return AdviceController.responseForbidden("Cannot submit this timesheet. It is already submitted.");
        else if(monthlyNote.getNote().length() == 0)
            return AdviceController.responseBadRequest("Cannot submit this timesheet. You need to write something in monthly note.");
        else{
            monthlyNote.setLocked(true);
            monthlyNoteService.save(monthlyNote);
            return AdviceController.responseOk("Time Sheet successfully submitted.");
        }
    }

    @PutMapping("/unlockTracking/{year}/{month}/{username}")
    public ResponseEntity<String> unlockTracking(@PathVariable int year,
                                                 @PathVariable int month,
                                                 @PathVariable String username,
                                                 Principal principal) throws ParseException {

        User userRepo = userService.find(principal.getName());
        if(!userRepo.getRole().getRoleName().equals(ADMIN_ROLE) && !principal.getName().equals(username))
            return AdviceController.responseForbidden("Forbidden action for user " + userRepo.getUsername());

        LocalDate passedDate = LocalDate.of(year, month, 1);
        Date date = new SimpleDateFormat(DATE_FORMAT_DDMMYYYY).parse("1-" + month + "-" + year);
        MonthlyNote monthlyNote = monthlyNoteService.find(username, date);
        userRepo = userService.find(username);
        if(userRepo == null)
            return AdviceController.responseNotFound("Cannot update " + username + " time sheet. (Not found)");
        else if(LocalDate.now().isAfter(passedDate.plusMonths(1).plusDays(25)))
            return AdviceController.responseForbidden(CANNOT_MODIFY_TIME_SHEET_STRING+ passedDate.plusMonths(1).plusDays(25));
        else if(monthlyNote == null)
            return AdviceController.responseNotFound("Cannot submit time sheet. (Not found)");
        else{
            monthlyNote.setLocked(false);
            monthlyNoteService.save(monthlyNote);
            return AdviceController.responseOk("Time Sheet successfully unlocked.");
        }
    }

    public LocalDate getEasterDate(int year) {
        int a = year % 19;
        int b = year / 100;
        int c = year % 100;
        int d = b / 4;
        int e = b % 4;
        int f = (b + 8) / 25;
        int g = (b - f + 1) / 3;
        int h = (19 * a + b - d - g + 15) % 30;
        int i = c / 4;
        int k = c % 4;
        int l = (32 + 2 * e + 2 * i - h - k) % 7;
        int m = (a + 11 * h + 22 * l) / 451;
        int easterMonth = (h + l - 7 * m + 114) / 31;
        int p = (h + l - 7 * m + 114) % 31;
        int easterDay = p + 1;
        return LocalDate.of(year, easterMonth, easterDay);
    }
}
