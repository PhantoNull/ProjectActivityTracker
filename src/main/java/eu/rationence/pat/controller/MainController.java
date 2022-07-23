package eu.rationence.pat.controller;

import eu.rationence.pat.model.*;
import eu.rationence.pat.service.*;
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

@Controller
public class MainController {
    private final UserService userService;
    private final StandardActivityService standardActivityService;
    private final RoleService roleService;
    private final TeamService teamService;
    private final UserActivityService userActivityService;
    private final ProjectTypeService projectTypeService;
    private final ProjectService projectService;
    private final ClientTypeService clientTypeService;
    private final ClientService clientService;
    private final ActivityTypeService activityTypeService;
    private final ProjectActivityService projectActivityService;
    private final LocationService locationService;

    @Autowired
    public MainController(UserService userService, StandardActivityService standardActivityService, RoleService roleService, TeamService teamService, UserActivityService userActivityService, ProjectTypeService projectTypeService, ProjectService projectService, ClientTypeService clientTypeService, ClientService clientService, ActivityTypeService activityTypeService, ProjectActivityService projectActivityService, LocationService locationService) {
        this.userService = userService;
        this.standardActivityService = standardActivityService;
        this.roleService = roleService;
        this.teamService = teamService;
        this.userActivityService = userActivityService;
        this.projectTypeService = projectTypeService;
        this.projectService = projectService;
        this.clientTypeService = clientTypeService;
        this.clientService = clientService;
        this.activityTypeService = activityTypeService;
        this.projectActivityService = projectActivityService;
        this.locationService = locationService;
    }

    @GetMapping ("/")
    public String index(Model model, Principal principal) {
        String username = principal.getName();
        User userRepo = userService.findUser(username);
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

    //Metodo inizializzante solo per dati default DB, da rimuovere in produzione
    @GetMapping("/initialize")
    public String initialize() throws ParseException {
        String[] stdNames = {"Stage","Ferie","ROL","Legge 104","Visita Medica", "Formazione Alunno", "Formazione Esterna",
                "Donazione Sangue", "Malattia", "Permesso Studio",
                "Permesso non retribuito", "Recupero", "Permesso", "Lutto", "Congedo Parentale Covid", "Permesso Cariche Elettive"};
        boolean[] stdInternal = {true, false, true, false, false, true, true, false, false, false, false, false, false, false,
                false, false};
        boolean[] stdWaged = {true, false, true, true, false, true, false, false, true, false, false, true, true, false, false, false};
        for(int i = 0; i < stdNames.length; i++){
            StandardActivity std = StandardActivity.builder().
                    activityKey(stdNames[i]).internal(stdInternal[i]).waged(stdWaged[i]).build();
            standardActivityService.save(std);
        }

        Role roleUser = Role.builder()
                .roleName("USER")
                .build();
        Role roleAdmin = Role.builder()
                .roleName("ADMIN")
                .build();
        roleService.save(roleUser);
        roleService.save(roleAdmin);


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
        userService.save(luca);

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
        userService.save(disabled);

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
        userService.save(marcon);

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
        ProjectType projInt = ProjectType.builder()
                .projectTypeKey("INT")
                .projectTypeDesc("Internal")
                .build();
        projectTypeService.save(projCons);
        projectTypeService.save(projDevp);
        projectTypeService.save(projTrng);
        projectTypeService.save(projServ);
        projectTypeService.save(projInt);

        ClientType clientDirect = ClientType.builder()
                .clientTypeKey("DIRECT")
                .build();
        ClientType clientPartner = ClientType.builder()
                .clientTypeKey("PARTNER")
                .build();
        ClientType clientInternal = ClientType.builder()
                .clientTypeKey("INTERNAL")
                .build();

        clientTypeService.save(clientDirect);
        clientTypeService.save(clientPartner);
        clientTypeService.save(clientInternal);

        Client clientBPER = Client.builder()
                .clientKey("BPER")
                .clientDesc("Banca Popolare Emilia Romagna")
                .clientType(clientDirect)
                .build();
        clientService.save(clientBPER);

        Client clientKNIME = Client.builder()
                .clientKey("KNIME")
                .clientDesc("Konstanz Information Miner")
                .clientType(clientPartner)
                .build();
        clientService.save(clientKNIME);

        Client clientCATT = Client.builder()
                .clientKey("CATT")
                .clientDesc("Cattolica")
                .clientType(clientDirect)
                .build();
        clientService.save(clientCATT);

        Client clientMICRO = Client.builder()
                .clientKey("MICRO")
                .clientDesc("Microstrategy")
                .clientType(clientPartner)
                .build();
        clientService.save(clientMICRO);

        Client clientRATIO = Client.builder()
                .clientKey("RATIO")
                .clientDesc("Rationence")
                .clientType(clientInternal)
                .build();
        clientService.save(clientRATIO);


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Project attInterne = Project.builder()
                .projectKey("RATIO-Int")
                .projectDesc("Att. Int.")
                .projectType(projInt)
                .client(clientRATIO)
                .projectManager(marcon)
                .team(ammTeam)
                .dateStart(sdf.parse("2022-04-01"))
                .value(0)
                .build();
        projectService.save(attInterne);

        Project orchBPER = Project.builder()
                .projectKey("BPER-ORCH-22")
                .projectDesc("BPER Orch")
                .projectType(projDevp)
                .client(clientBPER)
                .projectManager(marcon)
                .team(devTeam)
                .dateStart(sdf.parse("2022-04-01"))
                .dateEnd(sdf.parse("2022-08-01"))
                .dateClose(sdf.parse("2022-08-21"))
                .value(15000)
                .build();
        projectService.save(orchBPER);

        Project bohKNIME = Project.builder()
                .projectKey("KNIME-BOH-22")
                .projectDesc("KNIME Boh")
                .projectType(projDevp)
                .client(clientBPER)
                .projectManager(disabled)
                .team(anaTeam)
                .dateStart(sdf.parse("2022-03-11"))
                .value(12000)
                .build();
        projectService.save(bohKNIME);

        String[] actTypeKeyList = {"PM", "ANA", "DEV", "TEST", "BUG", "SUPP", "DOC", "QUAL", "AM", "TRNG", "TUTO", "INT", "TM"};
        String[] actTypeDescList = {"Project Management", "Analytics", "Development", "Testing", "Bug-Fixing", "Support",
                "Documenting", "Quality", "Application Maintenance", "Training", "Tutoring", "Internal", "T&M"};
        for(int i = 0; i < actTypeKeyList.length; i++){
            ActivityType aT = ActivityType.builder()
                    .activityTypeKey(actTypeKeyList[i]).activityTypeDesc(actTypeDescList[i]).build();
            activityTypeService.save(aT);
        }
        ActivityType devp = activityTypeService.find("DEV");
        ActivityType ana = activityTypeService.find("ANA");
        ActivityType internAct = activityTypeService.find("INT");

        ProjectActivity att = ProjectActivity.builder()
                .project(orchBPER.getProjectKey())
                .c_Project(orchBPER)
                .activityKey("DEV-22")
                .activityType(devp)
                .charged(true)
                .manDays(500)
                .dateStart(sdf.parse("2022-04-01"))
                .build();
        projectActivityService.save(att);

        ProjectActivity att2 = ProjectActivity.builder()
                .project(orchBPER.getProjectKey())
                .c_Project(orchBPER)
                .activityKey("DEV-22b")
                .activityType(devp)
                .charged(true)
                .manDays(300)
                .dateStart(sdf.parse("2022-06-01"))
                .build();
        projectActivityService.save(att2);
        UserActivity userAtt = UserActivity.builder()
                .username(luca.getUsername())
                .c_Username(luca)
                .activityKey(att2.getActivityKey())
                .c_Activity(att2)
                .project(att2.getProject())
                .dailyRate(100)
                .build();
        userActivityService.save(userAtt);

        ProjectActivity att3 = ProjectActivity.builder()
                .project(orchBPER.getProjectKey())
                .c_Project(orchBPER)
                .activityKey("ANA-22")
                .activityType(ana)
                .charged(true)
                .manDays(120)
                .dateStart(sdf.parse("2022-05-01"))
                .build();
        projectActivityService.save(att3);

        ProjectActivity attKNIME = ProjectActivity.builder()
                .project(bohKNIME.getProjectKey())
                .c_Project(bohKNIME)
                .activityKey("ANA-22")
                .activityType(ana)
                .charged(true)
                .manDays(150)
                .dateStart(sdf.parse("2022-05-01"))
                .build();
        projectActivityService.save(attKNIME);

        ProjectActivity attColloqui = ProjectActivity.builder()
                .project(attInterne.getProjectKey())
                .c_Project(attInterne)
                .activityKey("Colloqui")
                .activityType(internAct)
                .charged(false)
                .manDays(0)
                .dateStart(sdf.parse("2022-05-01"))
                .build();
        projectActivityService.save(attColloqui);

        ProjectActivity attFormazioneDocente = ProjectActivity.builder()
                .project(attInterne.getProjectKey())
                .c_Project(attInterne)
                .activityKey("Formaz. Docente")
                .activityType(internAct)
                .charged(false)
                .manDays(0)
                .dateStart(sdf.parse("2022-05-01"))
                .build();
        projectActivityService.save(attFormazioneDocente);

        UserActivity marconAtt = UserActivity.builder()
                .username(marcon.getUsername())
                .c_Username(marcon)
                .activityKey(attFormazioneDocente.getActivityKey())
                .c_Activity(attFormazioneDocente)
                .project(attFormazioneDocente.getProject())
                .dailyRate(200)
                .build();
        userActivityService.save(marconAtt);

        ProjectActivity attCoordinamento = ProjectActivity.builder()
                .project(attInterne.getProjectKey())
                .c_Project(attInterne)
                .activityKey("Coordinamento")
                .activityType(internAct)
                .charged(false)
                .manDays(0)
                .dateStart(sdf.parse("2022-05-01"))
                .build();
        projectActivityService.save(attCoordinamento);

        Location casa = Location.builder()
                .locationName("CASA").build();
        Location sede = Location.builder()
                .locationName("SEDE").build();
        Location trasf = Location.builder()
                .locationName("TRASFERTA").build();
        Location cliente = Location.builder()
                .locationName("CLIENTE").build();
        locationService.save(casa);
        locationService.save(sede);
        locationService.save(trasf);
        locationService.save(cliente);
        return "login";
    }

}
