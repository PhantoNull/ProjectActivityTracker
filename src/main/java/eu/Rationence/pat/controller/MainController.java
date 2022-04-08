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
import java.util.ArrayList;
import java.util.List;

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


    @GetMapping ("/")
    public String index() {
        return "index";
    }

    @GetMapping ("/teams")
    public String teams(Model model) {
        model.addAttribute("listaTeams", teamService.findAll());
        return "teams";
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
    public String initialize(){

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
                .projectType("CONS")
                .projectTypeDesc("Consulenza")
                .build();

        ProjectType projDevp = ProjectType.builder()
                .projectType("DEVP")
                .projectTypeDesc("Development")
                .build();

        ProjectType projTrng = ProjectType.builder()
                .projectType("TRNG")
                .projectTypeDesc("Training")
                .build();

        ProjectType projServ = ProjectType.builder()
                .projectType("SERV")
                .projectTypeDesc("Service")
                .build();
        projectTypeService.saveProjectType(projCons);
        projectTypeService.saveProjectType(projDevp);
        projectTypeService.saveProjectType(projTrng);
        projectTypeService.saveProjectType(projServ);

        ClientType clientDirect = ClientType.builder()
                .clientType("DIRECT")
                .build();
        ClientType clientPartner = ClientType.builder()
                .clientType("PARTNER")
                .build();
        ClientType clientInternal = ClientType.builder()
                .clientType("INTERNAL")
                .build();

        clientTypeService.saveClientType(clientDirect);
        clientTypeService.saveClientType(clientPartner);
        clientTypeService.saveClientType(clientInternal);

        Client clientBPER = Client.builder()
                .client("BPER")
                .clientDesc("Banca Popolare Emilia Romagna")
                .clientType(clientDirect)
                .build();
        clientService.saveClient(clientBPER);
        return "login";
    }


}
