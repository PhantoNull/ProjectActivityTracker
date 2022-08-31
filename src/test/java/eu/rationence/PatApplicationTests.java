package eu.rationence;

import eu.rationence.pat.PatApplication;
import eu.rationence.pat.model.*;
import eu.rationence.pat.service.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
	PatApplicationTests(RoleService roleService, ClientTypeService clientTypeService, ClientService clientService, ProjectTypeService projectTypeService, ProjectService projectService, TeamService teamService, UserService userService, LocationService locationService) {
		this.roleService = roleService;
		this.clientTypeService = clientTypeService;
		this.clientService = clientService;
		this.projectTypeService = projectTypeService;
		this.projectService = projectService;
		this.teamService = teamService;
		this.userService = userService;
		this.locationService = locationService;
	}

	@Test
	void roleServiceTest() {
		Role roleUser = Role.builder()
				.roleName("USER").build();
		Role roleAdmin = Role.builder()
				.roleName("ADMIN").build();
		Role roleToDelete = Role.builder()
				.roleName("ROLE_TO_DELETE").build();
		roleService.save(roleUser);
		roleService.save(roleAdmin);
		roleService.save(roleToDelete);
		assertEquals(roleAdmin, roleService.find("ADMIN"));
		assertEquals(roleUser, roleService.find("USER"));
		assertEquals(roleToDelete, roleService.find("ROLE_TO_DELETE"));
		roleService.delete("ROLE_TO_DELETE");
		assertNull(roleService.find("ROLE_TO_DELETE"));
	}

	@Test
	void clientTypeServiceTest() {
		ClientType clientDirect = ClientType.builder()
				.clientTypeKey("DIRECT")
				.build();
		ClientType clientPartner = ClientType.builder()
				.clientTypeKey("PARTNER")
				.build();
		ClientType clientInternal = ClientType.builder()
				.clientTypeKey("INTERNAL")
				.build();
		ClientType clientTypeToDelete = ClientType.builder()
				.clientTypeKey("TYPE_TO_DELETE")
				.build();
		clientTypeService.save(clientDirect);
		clientTypeService.save(clientPartner);
		clientTypeService.save(clientInternal);
		clientTypeService.save(clientTypeToDelete);
		assertEquals(clientDirect, clientTypeService.find("DIRECT"));
		assertEquals(clientPartner, clientTypeService.find("PARTNER"));
		assertEquals(clientInternal, clientTypeService.find("INTERNAL"));
		assertEquals(clientTypeToDelete, clientTypeService.find("TYPE_TO_DELETE"));
		clientTypeService.delete("TYPE_TO_DELETE");
		assertNull(clientTypeService.find("TYPE_TO_DELETE"));
	}

	@Test
	void projectTypeServiceTest() {
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
		ProjectType projDel = ProjectType.builder()
				.projectTypeKey("DEL")
				.projectTypeDesc("To be Deleted")
				.build();
		projectTypeService.save(projCons);
		projectTypeService.save(projDevp);
		projectTypeService.save(projTrng);
		projectTypeService.save(projServ);
		projectTypeService.save(projInt);
		projectTypeService.save(projDel);
		assertEquals(projCons, projectTypeService.find("CONS"));
		assertEquals(projDevp, projectTypeService.find("DEVP"));
		assertEquals(projTrng, projectTypeService.find("TRNG"));
		assertEquals(projServ, projectTypeService.find("SERV"));
		assertEquals(projInt, projectTypeService.find("INT"));
		assertEquals(projDel, projectTypeService.find("DEL"));
		projectTypeService.delete("DEL");
		assertNull(projectTypeService.find("DEL"));
	}

	@SneakyThrows
	@Test
	@WithMockUser(username = "Admin",authorities="ADMIN")
	void testClientsEndpointAdmin() {
		initializePAT();
		mockMvc.perform(get("/clients").with(csrf()))
				.andExpectAll(status().isOk());

		mockMvc.perform(post("/clients").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("clientKey", "CLKEY")
						.param("clientDesc", "Create")
						.param("clientType", "PARTNER"))
				.andExpectAll(status().isOk())
				.andDo(print());

		mockMvc.perform(get("/clients").with(csrf()))
				.andExpectAll(status().isOk())
				.andExpect(content().string(containsString("CLKEY")));

		mockMvc.perform(post("/clients").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("clientKey", "CLKEY")
						.param("clientDesc", "Trying Recreate")
						.param("clientType", "PARTNER"))
				.andExpectAll(status().is4xxClientError())
				.andDo(print());

		mockMvc.perform(put("/clients").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("clientKey", "CLKEY")
						.param("clientDesc", "Changing desc and type")
						.param("clientType", "DIRECT"))
				.andExpectAll(status().isOk())
				.andDo(print());

		mockMvc.perform(get("/clients").with(csrf()))
				.andExpectAll(status().isOk())
				.andExpect(content().string(containsString("Changing desc and type")));

		mockMvc.perform(delete("/clients").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("clientKey", "CLKEY")
						.param("clientDesc", "Changing desc and type")
						.param("clientType", "DIRECT"))
				.andExpectAll(status().isOk())
				.andDo(print());

		mockMvc.perform(delete("/clients").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("clientKey", "CLKEY"))
				.andExpectAll(status().is4xxClientError())
				.andDo(print());
	}

	@SneakyThrows
	@Test
	@WithMockUser(username = "User",authorities="USER")
	void testClientsEndpointUser() {
		initializePAT();
		mockMvc.perform(get("/clients").with(csrf()))
				.andExpectAll(status().is4xxClientError());

		mockMvc.perform(post("/clients").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("clientKey", "Prova")
						.param("clientDesc", "123456")
						.param("clientType", "PARTNER"))
				.andExpectAll((status().is4xxClientError()))
				.andDo(print());

		mockMvc.perform(put("/clients").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("clientKey", "Prova")
						.param("clientDesc", "123456")
						.param("clientType", "PARTNER"))
				.andExpectAll((status().is4xxClientError()))
				.andDo(print());

		mockMvc.perform(delete("/clients").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("clientKey", "Prova")
						.param("clientDesc", "123456")
						.param("clientType", "PARTNER"))
				.andExpectAll((status().is4xxClientError()))
				.andDo(print());
	}

	@SneakyThrows
	@Test
	@WithMockUser(username = "Admin",authorities="ADMIN")
	void testProjectsEndpointAdmin(){
		initializePAT();

		mockMvc.perform(post("/clients").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("clientKey", "CLKEY")
						.param("clientDesc", "Create")
						.param("clientType", "PARTNER"))
				.andExpectAll(status().isOk())
				.andDo(print());

		mockMvc.perform(post("/projects").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("projectKey", "PROJKEY")
						.param("projectDesc", "Descrizione Progetto")
						.param("client", "CLKEY")
						.param("projectType", "CONS")
						.param("dateStart", "2022-04-01")
						.param("team", "DEV")
						.param("projectManager", "User")
						.param("value","1000"))
				.andExpectAll((status().isOk()))
				.andDo(print());

		mockMvc.perform(post("/projects").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("projectKey", "PROJKEY")
						.param("projectDesc", "Retry create progetto")
						.param("client", "CLKEY")
						.param("projectType", "CONS")
						.param("dateStart", "2022-04-01")
						.param("team", "DEV")
						.param("projectManager", "User")
						.param("value","1000"))
				.andExpectAll((status().is4xxClientError()))
				.andDo(print());

		mockMvc.perform(put("/projects").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("projectKey", "PROJKEY")
						.param("projectDesc", "Modifica desc e type")
						.param("client", "CLKEY")
						.param("projectType", "DEVP")
						.param("dateStart", "2022-04-01")
						.param("team", "DEV")
						.param("projectManager", "User")
						.param("value","1000"))
				.andExpectAll((status().isOk()))
				.andDo(print());

		mockMvc.perform(delete("/projects").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("projectKey", "PROJKEY"))
				.andExpectAll((status().isOk()))
				.andDo(print());

		mockMvc.perform(delete("/projects").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.param("projectKey", "PROJKEY"))
				.andExpectAll((status().is4xxClientError()))
				.andDo(print());

		clientService.delete("CLKEY");
	}


	void initializePAT(){
		Role roleUser = Role.builder()
				.roleName("USER").build();
		Role roleAdmin = Role.builder()
				.roleName("ADMIN").build();
		roleService.save(roleUser);
		roleService.save(roleAdmin);

		Team devTeam = Team.builder()
				.teamName("DEV")
				.teamDesc("Developers")
				.teamAdmin("User")
				.build();
		Team ammTeam = Team.builder()
				.teamName("AMM")
				.teamDesc("Administration")
				.teamAdmin("Admin")
				.build();
		teamService.save(devTeam);
		teamService.save(ammTeam);

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		User user = User.builder()
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
		assertEquals(userService.find("User"), user);

		User admin = User.builder()
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
		assertEquals(userService.find("Admin"), admin);


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

		ProjectType projCons = ProjectType.builder()
				.projectTypeKey("CONS")
				.projectTypeDesc("Consulenza").build();
		ProjectType projDevp = ProjectType.builder()
				.projectTypeKey("DEVP")
				.projectTypeDesc("Development").build();
		ProjectType projTrng = ProjectType.builder()
				.projectTypeKey("TRNG")
				.projectTypeDesc("Training").build();
		ProjectType projServ = ProjectType.builder()
				.projectTypeKey("SERV")
				.projectTypeDesc("Service").build();
		ProjectType projInt = ProjectType.builder()
				.projectTypeKey("INT")
				.projectTypeDesc("Internal").build();
		projectTypeService.save(projCons);
		projectTypeService.save(projDevp);
		projectTypeService.save(projTrng);
		projectTypeService.save(projServ);
		projectTypeService.save(projInt);
	}
}

