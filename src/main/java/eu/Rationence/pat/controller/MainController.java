package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.*;
import eu.Rationence.pat.service.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
@AllArgsConstructor
public class MainController {
    @Autowired
    private final UserService userService;
    @Autowired
    private final TeamService teamService;
    @Autowired
    private final RoleService roleService;
    @Autowired
    private final ProjectTypeService projectTypeService;
    @Autowired
    private final ClientTypeService clientTypeService;
    @Autowired
    private final ClientService clientService;
    @Autowired
    private final StandardActivityService standardActivityService;
    @Autowired
    private final ProjectService projectService;
    @Autowired
    private final ActivityTypeService activityTypeService;
    @Autowired
    private final ActivityService activityService;


    @GetMapping ("/")
    public String index(Model model, Principal principal) {
        String username = principal.getName();
        User userRepo = userService.findUserByUsername(username);
        model.addAttribute("userTeam", userRepo.getTeam().getTeamName());
        model.addAttribute("userTeamName", userRepo.getTeam().getTeamDesc());
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping(value="/logout")
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/login?logout";
    }

    @GetMapping("/initialize")
    public String initialize() throws ParseException {
        String[] stdNames = {"Stage","Ferie","Rol","Legge 104","Visita Medica", "Formazione Alunno", "Formazione Docente", "Formazione Esterna",
                            "Attività Interne", "Donazione Sangue", "Presidio Reply", "Colloqui", "Coordinamento", "Malattia", "Permessio Studio",
                            "Permesso non retribuito", "Recupero", "Permesso", "Lutto", "Congedo Parentale Covid", "Permesso Cariche Elettive"};
        boolean[] stdInternal = {true, false, true, false, false, true, true, true, true, false, false, true, true, false, false, false, false, false, false,
                                 false, false};
        boolean[] stdWaged = {true, false, true, true, false, true, true, true, false, false, true, false, false, true, false, false, true, true, false, false, false};
        for(int i = 0; i < stdNames.length; i++){
            StandardActivity std = StandardActivity.builder().
                    activityKey(stdNames[i]).internal(stdInternal[i]).waged(stdWaged[i]).build();
            standardActivityService.saveStdActivity(std);
        }

        Role roleUser = Role.builder()
                .roleName("USER")
                .build();
        Role roleAdmin = Role.builder()
                .roleName("ADMIN")
                .build();
        roleService.saveRole(roleUser);
        roleService.saveRole(roleAdmin);


        Team devTeam = Team.builder()
                .teamName("DEV")
                .teamDesc("Sviluppatori")
                .teamAdmin("Luca.DiPierro")
                .build();
        Team ammTeam = Team.builder()
                .teamName("AMM")
                .teamDesc("Amministrazione")
                .teamAdmin("Giuseppe.Marcon")
                .build();
        Team anaTeam = Team.builder()
                .teamName("ANA")
                .teamDesc("Analytics")
                .teamAdmin("Marco.Rossi")
                .build();
        teamService.saveTeam(devTeam);
        teamService.saveTeam(ammTeam);
        teamService.saveTeam(anaTeam);


        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        List<Role> roleList = new ArrayList();
        roleList.add(roleUser);
        User luca = User.builder()
                .username("Luca.DiPierro")
                .name("Luca")
                .surname("Di Pierro")
                .description("Luca Di Pierro")
                .email("luca.dipierro@rationence.eu")
                .role(roleUser)
                .team(devTeam)
                .cost(100)
                .passwordHash(encoder.encode("password90!"))
                .time("88888")
                .enabled(true)
                .build();
        userService.saveUser(luca);

        User disabled = User.builder()
                .username("Marco.Rossi")
                .name("Marco")
                .surname("Rossi")
                .description("Marco Rossi")
                .email("marco.rossi@rationence.eu")
                .role(roleUser)
                .team(anaTeam)
                .cost(100)
                .passwordHash(encoder.encode("password"))
                .time("66006")
                .enabled(false)
                .build();
        userService.saveUser(disabled);

        roleList.add(roleAdmin);
        User marcon = User.builder()
                .username("Giuseppe.Marcon")
                .name("Giuseppe")
                .surname("Marcon")
                .description("Giuseppe Marcon")
                .email("giuseppe.marcon@rationence.eu")
                .role(roleAdmin)
                .team(ammTeam)
                .cost(200)
                .passwordHash(encoder.encode("passwordmarcon"))
                .time("88888")
                .enabled(true)
                .build();
        userService.saveUser(marcon);

        teamService.saveTeam(devTeam);
        teamService.saveTeam(ammTeam);

        ProjectType projCons = ProjectType.builder()
                .projectTypeKey("CONS")
                .projectTypeDesc("Consulenza")
                .build();

        ProjectType projDevp = ProjectType.builder()
                .projectTypeKey("DEVP")
                .projectTypeDesc("Development")
                .build();

        ProjectType projTrng = ProjectType.builder()
                .projectTypeKey("TRNG")
                .projectTypeDesc("Training")
                .build();

        ProjectType projServ = ProjectType.builder()
                .projectTypeKey("SERV")
                .projectTypeDesc("Service")
                .build();
        projectTypeService.saveProjectType(projCons);
        projectTypeService.saveProjectType(projDevp);
        projectTypeService.saveProjectType(projTrng);
        projectTypeService.saveProjectType(projServ);

        ClientType clientDirect = ClientType.builder()
                .clientTypeKey("DIRECT")
                .build();
        ClientType clientPartner = ClientType.builder()
                .clientTypeKey("PARTNER")
                .build();
        ClientType clientInternal = ClientType.builder()
                .clientTypeKey("INTERNAL")
                .build();

        clientTypeService.saveClientType(clientDirect);
        clientTypeService.saveClientType(clientPartner);
        clientTypeService.saveClientType(clientInternal);

        Client clientBPER = Client.builder()
                .clientKey("BPER")
                .clientDesc("Banca Popolare Emilia Romagna")
                .clientType(clientDirect)
                .build();
        clientService.saveClient(clientBPER);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Project orchBPER = Project.builder()
                .projectKey("BPERORCH22")
                .projectDesc("Orchestratore BPER")
                .projectType(projDevp)
                .client(clientBPER)
                .projectManager(marcon)
                .team(devTeam)
                .dateStart(sdf.parse("2022-04-01"))
                .dateEnd(sdf.parse("2023-04-01"))
                .dateClose(sdf.parse("2024-04-01"))
                .value(15000)
                .build();
        projectService.saveProject(orchBPER);

        String[] actTypeKeyList = {"PM", "ANA", "DEV", "TEST", "BUG", "SUPP", "DOC", "QUAL", "AM", "TRNG", "TUTO"};
        String[] actTypeDescList = {"Project Management", "Analytics", "Development", "Testing", "Bug-Fixing", "Support",
                                    "Documenting", "Quality", "Application Maintenance", "Training", "Tutoring"};
        for(int i = 0; i < actTypeKeyList.length; i++){
            ActivityType aT = ActivityType.builder()
                    .activityTypeKey(actTypeKeyList[i]).activityTypeDesc(actTypeDescList[i]).build();
            activityTypeService.saveActivityType(aT);
        }
        ActivityType devp = ActivityType.builder().activityTypeKey("DEV").activityTypeDesc("Development").build();

        Activity att = Activity.builder()
                .projectId(orchBPER.getProjectKey())
                .project(orchBPER)
                .activityKey("DEV-22")
                .activityType(devp)
                .charged(true)
                .manDays(500)
                .dateStart(sdf.parse("2022-04-01"))
                .build();
        activityService.saveActivity(att);
        return "login";
    }

}
