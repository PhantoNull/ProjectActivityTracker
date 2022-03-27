package eu.Rationence.pat.controller;

import eu.Rationence.pat.model.ProjectType;
import eu.Rationence.pat.model.Role;
import eu.Rationence.pat.model.Team;
import eu.Rationence.pat.model.User;
import eu.Rationence.pat.service.ProjectTypeService;
import eu.Rationence.pat.service.RoleService;
import eu.Rationence.pat.service.TeamService;
import eu.Rationence.pat.service.UtentiService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
public class MainController {
    @Autowired
    private final UtentiService utentiService;
    @Autowired
    private final TeamService teamService;
    @Autowired
    private final RoleService roleService;
    @Autowired
    private final ProjectTypeService projectTypeService;

    @RequestMapping ("/")
    public String index() {
        return "index.html";
    }

    @GetMapping ("/utenti")
    public String utenti(Model model) {
        model.addAttribute("listaUtenti", utentiService.findAll());
        return "utenti";
    }

    @GetMapping ("/teams")
    public String teams(Model model) {
        model.addAttribute("listaTeams", teamService.findAll());
        return "teams";
    }

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
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
                .role("USER")
                .build();
        Role roleAdmin = Role.builder()
                .role("ADMIN")
                .build();
        roleService.saveRole(roleUser);
        roleService.saveRole(roleAdmin);


        Team devTeam = Team.builder()
                .teamName("DEV")
                .teamDesc("Sviluppatori")
                .build();
        Team ammTeam = Team.builder()
                .teamName("AMM")
                .teamDesc("Amministrazione")
                .build();
        Team anaTeam = Team.builder()
                .teamName("ANA")
                .teamDesc("Analytics")
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
                .cost(100.0)
                .passwordHash(encoder.encode("password90!"))
                .time("88888")
                .enabled(true)
                .build();
        utentiService.saveUser(luca);

        User disabled = User.builder()
                .username("marco.rossi")
                .name("Marco")
                .surname("Rossi")
                .description("Marco Rossi")
                .email("marco.rossi@rationence.eu")
                .role(roleUser)
                .team(anaTeam)
                .cost(100.0)
                .passwordHash(encoder.encode("password"))
                .time("66006")
                .enabled(false)
                .build();
        utentiService.saveUser(disabled);

        roleList.add(roleAdmin);
        User marcon = User.builder()
                .username("Giuseppe.Marcon")
                .name("Giuseppe")
                .surname("Marcon")
                .description("Giuseppe Marcon")
                .email("giuseppe.marcon@rationence.eu")
                .role(roleAdmin)
                .team(ammTeam)
                .cost(200.0)
                .passwordHash(encoder.encode("passwordmarcon"))
                .time("88888")
                .enabled(true)
                .build();
        utentiService.saveUser(marcon);

        devTeam.setAdministrator(luca);
        ammTeam.setAdministrator(marcon);
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

        return "login";
    }


}
