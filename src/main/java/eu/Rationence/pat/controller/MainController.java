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
    private final ProjectActivityService projectActivityService;
    @Autowired
    private final UserActivityService userActivityService;
    @Autowired
    private final LocationService locationService;


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

        Client clientKNIME = Client.builder()
                .clientKey("KNIME")
                .clientDesc("Konstanz Information Miner")
                .clientType(clientPartner)
                .build();
        clientService.saveClient(clientKNIME);

        Client clientCATT = Client.builder()
                .clientKey("CATT")
                .clientDesc("Cattolica")
                .clientType(clientDirect)
                .build();
        clientService.saveClient(clientCATT);

        Client clientMICRO = Client.builder()
                .clientKey("MICRO")
                .clientDesc("Microstrategy")
                .clientType(clientPartner)
                .build();
        clientService.saveClient(clientMICRO);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Project orchBPER = Project.builder()
                .projectKey("BPER-ORCH-22")
                .projectDesc("Orchestratore BPER")
                .projectType(projDevp)
                .client(clientBPER)
                .projectManager(marcon)
                .team(devTeam)
                .dateStart(sdf.parse("2022-04-01"))
                .dateEnd(sdf.parse("2022-08-01"))
                .dateClose(sdf.parse("2022-08-21"))
                .value(15000)
                .build();
        projectService.saveProject(orchBPER);

        Project bohKNIME = Project.builder()
                .projectKey("KNIME-BOH-22")
                .projectDesc("Progetto boh")
                .projectType(projDevp)
                .client(clientBPER)
                .projectManager(disabled)
                .team(anaTeam)
                .dateStart(sdf.parse("2022-03-11"))
                .value(12000)
                .build();
        projectService.saveProject(bohKNIME);

        String[] actTypeKeyList = {"PM", "ANA", "DEV", "TEST", "BUG", "SUPP", "DOC", "QUAL", "AM", "TRNG", "TUTO"};
        String[] actTypeDescList = {"Project Management", "Analytics", "Development", "Testing", "Bug-Fixing", "Support",
                                    "Documenting", "Quality", "Application Maintenance", "Training", "Tutoring"};
        for(int i = 0; i < actTypeKeyList.length; i++){
            ActivityType aT = ActivityType.builder()
                    .activityTypeKey(actTypeKeyList[i]).activityTypeDesc(actTypeDescList[i]).build();
            activityTypeService.saveActivityType(aT);
        }
        ActivityType devp = activityTypeService.findActivityTypeByActivityType("DEV");
        ActivityType ana = activityTypeService.findActivityTypeByActivityType("ANA");

        ProjectActivity att = ProjectActivity.builder()
                .project(orchBPER.getProjectKey())
                .c_Project(orchBPER)
                .activityKey("DEV-22")
                .activityType(devp)
                .charged(true)
                .manDays(500)
                .dateStart(sdf.parse("2022-04-01"))
                .build();
        projectActivityService.saveActivity(att);

        ProjectActivity att2 = ProjectActivity.builder()
                .project(orchBPER.getProjectKey())
                .c_Project(orchBPER)
                .activityKey("DEV-22b")
                .activityType(devp)
                .charged(true)
                .manDays(300)
                .dateStart(sdf.parse("2022-06-01"))
                .build();
        projectActivityService.saveActivity(att2);
        UserActivity userAtt = UserActivity.builder()
                .username(luca.getUsername())
                .c_Username(luca)
                .activityKey(att2.getActivityKey())
                .c_Activity(att2)
                .project(att2.getProject())
                .dailyRate(100)
                .build();
        userActivityService.saveUserActivity(userAtt);

        ProjectActivity att3 = ProjectActivity.builder()
                .project(orchBPER.getProjectKey())
                .c_Project(orchBPER)
                .activityKey("ANA-22")
                .activityType(ana)
                .charged(false)
                .manDays(120)
                .dateStart(sdf.parse("2022-05-01"))
                .build();
        projectActivityService.saveActivity(att3);

        ProjectActivity attKNIME = ProjectActivity.builder()
                .project(bohKNIME.getProjectKey())
                .c_Project(bohKNIME)
                .activityKey("ANA-22")
                .activityType(ana)
                .charged(false)
                .manDays(150)
                .dateStart(sdf.parse("2022-05-01"))
                .build();
        projectActivityService.saveActivity(attKNIME);

        Location casa = Location.builder()
                .locationName("CASA").build();
        Location sede = Location.builder()
                .locationName("SEDE").build();
        Location trasf = Location.builder()
                .locationName("TRASFERTA").build();
        Location cliente = Location.builder()
                .locationName("CLIENTE").build();
        locationService.saveLocation(casa);
        locationService.saveLocation(sede);
        locationService.saveLocation(trasf);
        locationService.saveLocation(cliente);
        return "login";
    }

}
