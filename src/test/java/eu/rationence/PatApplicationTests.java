package eu.rationence;

import eu.rationence.pat.PatApplication;
import eu.rationence.pat.model.*;
import eu.rationence.pat.service.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = PatApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PatApplicationTests {
  private final RoleService roleService;
  private final ClientTypeService clientTypeService;
  private final ClientService clientService;
  private final ProjectTypeService projectTypeService;
  private final ProjectService projectService;
  private final TeamService teamService;
  private final UserService userService;
  private final LocationService locationService;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  PatApplicationTests(
      RoleService roleService,
      ClientTypeService clientTypeService,
      ClientService clientService,
      ProjectTypeService projectTypeService,
      ProjectService projectService,
      TeamService teamService,
      UserService userService,
      LocationService locationService) {
    this.roleService = roleService;
    this.clientTypeService = clientTypeService;
    this.clientService = clientService;
    this.projectTypeService = projectTypeService;
    this.projectService = projectService;
    this.teamService = teamService;
    this.userService = userService;
    this.locationService = locationService;
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "Admin", authorities = "ADMIN")
  void testRolesEndpointAdmin() {
    mockMvc
        .perform(
            post("/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleName", "TROLE"))
        .andExpectAll(status().isOk());

    mockMvc
        .perform(
            post("/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleName", "TROLE"))
        .andExpectAll(status().is4xxClientError());

    mockMvc
        .perform(
            delete("/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleName", "TROLE"))
        .andExpectAll(status().isOk());

    mockMvc
        .perform(
            delete("/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleName", "TROLE"))
        .andExpectAll(status().is4xxClientError());
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "User", authorities = "USER")
  void testRolesEndpointUser() {

    mockMvc
        .perform(
            post("/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleName", "TROLE"))
        .andExpectAll(status().is4xxClientError());

    mockMvc
        .perform(
            delete("/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("roleName", "TROLE"))
        .andExpectAll(status().is4xxClientError());
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "Admin", authorities = "ADMIN")
  void testStandardActivitiesEndpointAdmin() {
    mockMvc.perform(get("/standardactivities").with(csrf())).andExpectAll(status().isOk());

    mockMvc
        .perform(
            post("/standardactivities")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("activityKey", "ATTKEY")
                .param("waged", "true")
                .param("internal", "false"))
        .andExpectAll(status().isOk());

    mockMvc
        .perform(
            post("/standardactivities")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("activityKey", "ATTKEY")
                .param("waged", "true")
                .param("internal", "false"))
        .andExpectAll(status().is4xxClientError());

    mockMvc
        .perform(
            put("/standardactivities")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("activityKey", "ATTKEY")
                .param("waged", "false")
                .param("internal", "true"))
        .andExpectAll(status().isOk());

    mockMvc
        .perform(
            delete("/standardactivities")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("activityKey", "ATTKEY"))
        .andExpectAll(status().isOk());

    mockMvc
        .perform(
            delete("/standardactivities")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("activityKey", "ATTKEY"))
        .andExpectAll(status().is4xxClientError());
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "User", authorities = "USER")
  void testStandardActivitiesEndpointUser() {
    mockMvc
        .perform(get("/standardactivities").with(csrf()))
        .andExpectAll(status().is4xxClientError());

    mockMvc
        .perform(
            post("/standardactivities")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("activityKey", "ATTKEY")
                .param("waged", "true")
                .param("internal", "false"))
        .andExpectAll(status().is4xxClientError());

    mockMvc
        .perform(
            put("/standardactivities")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("activityKey", "ATTKEY")
                .param("waged", "false")
                .param("internal", "true"))
        .andExpectAll(status().is4xxClientError());

    mockMvc
        .perform(
            delete("/standardactivities")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("activityKey", "ATTKEY"))
        .andExpectAll(status().is4xxClientError());
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "Admin", authorities = "ADMIN")
  void testClientsEndpointAdmin() {
    mockMvc.perform(get("/clients").with(csrf())).andExpectAll(status().isOk());

    mockMvc
        .perform(
            post("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "CLKEY")
                .param("clientDesc", "Create")
                .param("clientType", "PARTNER"))
        .andExpectAll(status().isOk());

    mockMvc
        .perform(get("/clients").with(csrf()))
        .andExpectAll(status().isOk())
        .andExpect(content().string(containsString("CLKEY")));

    mockMvc
        .perform(
            post("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "CLKEY")
                .param("clientDesc", "Trying Recreate")
                .param("clientType", "PARTNER"))
        .andExpectAll(status().is4xxClientError());

    mockMvc
        .perform(
            put("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "CLKEY")
                .param("clientDesc", "Changing desc and type")
                .param("clientType", "DIRECT"))
        .andExpectAll(status().isOk());

    mockMvc
        .perform(get("/clients").with(csrf()))
        .andExpectAll(status().isOk())
        .andExpect(content().string(containsString("Changing desc and type")));

    mockMvc
        .perform(
            delete("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "CLKEY")
                .param("clientDesc", "Changing desc and type")
                .param("clientType", "DIRECT"))
        .andExpectAll(status().isOk());

    mockMvc
        .perform(
            delete("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "CLKEY"))
        .andExpectAll(status().is4xxClientError());
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "User", authorities = "USER")
  void testClientsEndpointUser() {
    mockMvc.perform(get("/clients").with(csrf())).andExpectAll(status().is4xxClientError());

    mockMvc
        .perform(
            post("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "Prova")
                .param("clientDesc", "123456")
                .param("clientType", "PARTNER"))
        .andExpectAll((status().is4xxClientError()));

    mockMvc
        .perform(
            put("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "Prova")
                .param("clientDesc", "123456")
                .param("clientType", "PARTNER"))
        .andExpectAll((status().is4xxClientError()));

    mockMvc
        .perform(
            delete("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "Prova")
                .param("clientDesc", "123456")
                .param("clientType", "PARTNER"))
        .andExpectAll((status().is4xxClientError()));
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "Admin", authorities = "ADMIN")
  void testProjectsEndpointAdmin() {

    mockMvc
        .perform(
            post("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "CLKEY")
                .param("clientDesc", "Create")
                .param("clientType", "PARTNER"))
        .andExpectAll(status().isOk())
        .andDo(print());

    mockMvc
        .perform(
            post("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("projectKey", "PROJKEY")
                .param("projectDesc", "Descrizione Progetto")
                .param("client", "CLKEY")
                .param("projectType", "CONS")
                .param("dateStart", "2022-04-01")
                .param("team", "DEV")
                .param("projectManager", "User")
                .param("value", "1000"))
        .andExpectAll((status().isOk()));

    mockMvc
        .perform(
            post("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("projectKey", "PROJKEY")
                .param("projectDesc", "Retry create progetto")
                .param("client", "CLKEY")
                .param("projectType", "CONS")
                .param("dateStart", "2022-04-01")
                .param("team", "DEV")
                .param("projectManager", "User")
                .param("value", "1000"))
        .andExpectAll((status().is4xxClientError()));

    mockMvc
        .perform(
            put("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("projectKey", "PROJKEY")
                .param("projectDesc", "Modifica desc e type")
                .param("client", "CLKEY")
                .param("projectType", "DEVP")
                .param("dateStart", "2022-04-01")
                .param("team", "DEV")
                .param("projectManager", "User")
                .param("value", "1000"))
        .andExpectAll((status().isOk()));

    mockMvc
        .perform(
            delete("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("projectKey", "PROJKEY"))
        .andExpectAll((status().isOk()));

    mockMvc
        .perform(
            delete("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("projectKey", "PROJKEY"))
        .andExpectAll((status().is4xxClientError()));

    mockMvc
        .perform(
            delete("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "CLKEY"))
        .andExpectAll(status().isOk())
        .andDo(print());
  }

  @SneakyThrows
  @Test
  @WithMockUser(username = "User", authorities = "USER")
  void testProjectsEndpointUser() {
    mockMvc
        .perform(get("/clients").with(csrf()))
        .andExpectAll(status().is4xxClientError())
        .andDo(print());

    mockMvc
        .perform(
            post("/clients")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("clientKey", "CLKEY")
                .param("clientDesc", "Create")
                .param("clientType", "PARTNER"))
        .andExpectAll(status().is4xxClientError());

    mockMvc
        .perform(
            post("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("projectKey", "PROJKEY")
                .param("projectDesc", "Descrizione Progetto")
                .param("client", "CLKEY")
                .param("projectType", "CONS")
                .param("dateStart", "2022-04-01")
                .param("team", "DEV")
                .param("projectManager", "User")
                .param("value", "1000"))
        .andExpectAll((status().is4xxClientError()));

    mockMvc
        .perform(
            post("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("projectKey", "PROJKEY")
                .param("projectDesc", "Retry create progetto")
                .param("client", "CLKEY")
                .param("projectType", "CONS")
                .param("dateStart", "2022-04-01")
                .param("team", "DEV")
                .param("projectManager", "User")
                .param("value", "1000"))
        .andExpectAll((status().is4xxClientError()));

    mockMvc
        .perform(
            put("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("projectKey", "PROJKEY")
                .param("projectDesc", "Modifica desc e type")
                .param("client", "CLKEY")
                .param("projectType", "DEVP")
                .param("dateStart", "2022-04-01")
                .param("team", "DEV")
                .param("projectManager", "User")
                .param("value", "1000"))
        .andExpectAll((status().is4xxClientError()));

    mockMvc
        .perform(
            delete("/projects")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .param("projectKey", "PROJKEY"))
        .andExpectAll((status().is4xxClientError()));
  }

  @Test
  @BeforeAll
  void initializePAT() {
    Role roleUser = Role.builder().roleName("USER").build();
    Role roleAdmin = Role.builder().roleName("ADMIN").build();
    roleService.save(roleUser);
    roleService.save(roleAdmin);
    assertEquals(roleUser, roleService.find("USER"));
    assertEquals(roleAdmin, roleService.find("ADMIN"));
    ArrayList<Role> roleList = new ArrayList();
    roleList.add(roleAdmin);
    roleList.add(roleUser);
    assertEquals(roleList, roleService.findAll());

    Team devTeam = Team.builder().teamName("DEV").teamDesc("Developers").teamAdmin("User").build();
    Team ammTeam =
        Team.builder().teamName("AMM").teamDesc("Administration").teamAdmin("Admin").build();
    teamService.save(devTeam);
    teamService.save(ammTeam);
    assertEquals(devTeam, teamService.find("DEV"));
    assertEquals(ammTeam, teamService.find("AMM"));

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    User user =
        User.builder()
            .username("User")
            .name("UserUsername")
            .surname("UserSurname")
            .description("UserUsername UserSurname")
            .email("user.email@rationence.eu")
            .role(roleUser)
            .team(devTeam)
            .cost(100)
            .passwordHash(encoder.encode("passwordUser"))
            .time("88888")
            .enabled(true)
            .build();
    userService.save(user);
    User admin =
        User.builder()
            .username("Admin")
            .name("AdminUsername")
            .surname("AdminSurname")
            .description("AdminUsername AdminSurname")
            .email("admin.email@rationence.eu")
            .role(roleUser)
            .team(ammTeam)
            .cost(200)
            .passwordHash(encoder.encode("passwordAdmin"))
            .time("88888")
            .enabled(true)
            .build();
    userService.save(admin);
    assertEquals(user, userService.find("User"));
    assertEquals(admin, userService.find("Admin"));

    ClientType clientDirect = ClientType.builder().clientTypeKey("DIRECT").build();
    ClientType clientPartner = ClientType.builder().clientTypeKey("PARTNER").build();
    ClientType clientInternal = ClientType.builder().clientTypeKey("INTERNAL").build();
    ClientType clientDelete = ClientType.builder().clientTypeKey("DELETE").build();
    clientTypeService.save(clientDirect);
    clientTypeService.save(clientPartner);
    clientTypeService.save(clientInternal);
    clientTypeService.save(clientDelete);
    assertEquals(clientDirect, clientTypeService.find("DIRECT"));
    assertEquals(clientPartner, clientTypeService.find("PARTNER"));
    assertEquals(clientInternal, clientTypeService.find("INTERNAL"));
    assertEquals(clientDelete, clientTypeService.find("DELETE"));
    clientTypeService.delete("DELETE");
    assertNull(clientTypeService.find("DELETE"));

    Location casa = Location.builder().locationName("CASA").build();
    Location sede = Location.builder().locationName("SEDE").build();
    Location trasf = Location.builder().locationName("TRASFERTA").build();
    Location cliente = Location.builder().locationName("CLIENTE").build();
    Location delete = Location.builder().locationName("DELETE").build();
    locationService.save(casa);
    locationService.save(sede);
    locationService.save(trasf);
    locationService.save(cliente);
    locationService.save(delete);
    assertEquals(casa, locationService.find("CASA"));
    assertEquals(sede, locationService.find("SEDE"));
    assertEquals(trasf, locationService.find("TRASFERTA"));
    assertEquals(cliente, locationService.find("CLIENTE"));
    assertEquals(delete, locationService.find("DELETE"));
    locationService.delete("DELETE");
    assertNull(locationService.find("DELETE"));

    ProjectType projCons =
        ProjectType.builder().projectTypeKey("CONS").projectTypeDesc("Consulenza").build();
    ProjectType projDevp =
        ProjectType.builder().projectTypeKey("DEVP").projectTypeDesc("Development").build();
    ProjectType projTrng =
        ProjectType.builder().projectTypeKey("TRNG").projectTypeDesc("Training").build();
    ProjectType projServ =
        ProjectType.builder().projectTypeKey("SERV").projectTypeDesc("Service").build();
    ProjectType projInt =
        ProjectType.builder().projectTypeKey("INT").projectTypeDesc("Internal").build();
    ProjectType projDel =
        ProjectType.builder().projectTypeKey("DEL").projectTypeDesc("Delete").build();
    projectTypeService.save(projCons);
    projectTypeService.save(projDevp);
    projectTypeService.save(projTrng);
    projectTypeService.save(projServ);
    projectTypeService.save(projInt);
    projectTypeService.save(projDel);
    assertEquals(projCons, projectTypeService.find("CONS"));
    assertEquals(projDevp, projectTypeService.find("DEVP"));
    assertEquals(projDel, projectTypeService.find("DEL"));
    projectTypeService.delete("DEL");
    assertNull(projectTypeService.find("DEL"));
  }
}
