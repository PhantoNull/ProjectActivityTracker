package eu.rationence;

import eu.rationence.pat.PatApplication;
import eu.rationence.pat.model.*;
import eu.rationence.pat.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = PatApplication.class)
class PatApplicationTests {
	private final RoleService roleService;
	private final ClientTypeService clientTypeService;
	private final ClientService clientService;
	private final ProjectTypeService projectTypeService;
	private final ProjectService projectService;
	private final TeamService teamService;
	private final UserService userService;
	private final LocationService locationService;
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
		assertNull(roleService.find("ADMIN"));
		assertNull(roleService.find("USER"));
		assertNull(roleService.find("ROLE_TO_DELETE"));
		Role roleUser = Role.builder()
				.roleName("USER")
				.build();
		Role roleAdmin = Role.builder()
				.roleName("ADMIN")
				.build();
		Role roleToDelete = Role.builder()
				.roleName("ROLE_TO_DELETE")
				.build();
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
	void clientTypeServiceTest(){
		assertNull(clientTypeService.find("DIRECT"));
		assertNull(clientTypeService.find("PARTNER"));
		assertNull(clientTypeService.find("INTERNAL"));
		assertNull(clientTypeService.find("TYPE_TO_DELETE"));
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
	void clientServiceTest(){
		assertNull(clientService.find("BPER"));
		assertNull(clientService.find("KNIME"));
		assertNull(clientService.find("RATIO"));
		assertNull(clientService.find("CLIENT_TO_DELETE"));
		ClientType clientDirect = clientTypeService.find("DIRECT");
		ClientType clientPartner = clientTypeService.find("PARTNER");
		ClientType clientInternal = clientTypeService.find("INTERNAL");
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

		Client clientRATIO = Client.builder()
				.clientKey("RATIO")
				.clientDesc("Rationence")
				.clientType(clientInternal)
				.build();
		clientService.save(clientRATIO);

		Client clientToDelete = Client.builder()
				.clientKey("DEL")
				.clientDesc("Client To Delete")
				.clientType(clientInternal)
				.build();
		clientService.save(clientToDelete);
		assertEquals(clientBPER, clientService.find("BPER"));
		assertEquals(clientKNIME, clientService.find("KNIME"));
		assertEquals(clientRATIO, clientService.find("RATIO"));
		assertEquals(clientToDelete, clientService.find("DEL"));
		clientService.delete("DEL");
		assertNull(clientService.find("DEL"));
	}

	@Test
	void projectTypeServiceTest(){
		assertNull(projectTypeService.find("CONS"));
		assertNull(projectTypeService.find("DEVP"));
		assertNull(projectTypeService.find("TRNG"));
		assertNull(projectTypeService.find("SERV"));
		assertNull(projectTypeService.find("INT"));
		assertNull(projectTypeService.find("DEL"));
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

	@Test
	void teamAndUserServiceTest(){
		assertNull(teamService.find("DEV"));
		assertNull(teamService.find("AMM"));
		assertNull(teamService.find("ANA"));
		assertNull(teamService.find("DEL"));
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
		Team delTeam = Team.builder()
				.teamName("DEL")
				.teamDesc("To Delete")
				.teamAdmin("Marco.Rossi")
				.build();
		teamService.save(devTeam);
		teamService.save(ammTeam);
		teamService.save(anaTeam);
		teamService.save(delTeam);
		assertEquals(ammTeam, teamService.find("AMM"));
		assertEquals(devTeam, teamService.find("DEV"));
		assertEquals(anaTeam, teamService.find("ANA"));
		assertEquals(delTeam, teamService.find("DEL"));
		teamService.deleteTeam("DEL");
		assertNull(teamService.find("DEL"));

		assertNull(userService.find("Luca.DiPierro"));
		assertNull(userService.find("Giuseppe.Marcon"));
		assertNull(userService.find("Marco.Rossi"));
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		Role roleUser = roleService.find("USER");
		Role roleAdmin = roleService.find("ADMIN");
		System.out.println(devTeam);
		System.out.println(roleUser);
		User luca = User.builder()
				.username("Luca.DiPierro")
				.name("Luca")
				.surname("Di Pierro")
				.description("Luca Di Pierro")
				.email("luca.dipierro@rationence.eu")
				.role(roleUser)
				.team(ammTeam)
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
		assertEquals(marcon, userService.find("Giuseppe.Marcon"));
		assertEquals(luca, userService.find("Luca.DiPierro"));
		assertEquals(disabled, userService.find("Marco.Rossi"));
		userService.delete("Marco.Rossi");
		assertNull(userService.find("Marco.Rossi"));
	}

	@Test
	void locationServiceTest(){
		assertNull(locationService.find("CASA"));
		assertNull(locationService.find("SEDE"));
		assertNull(locationService.find("TRASFERTA"));
		assertNull(locationService.find("CLIENTE"));
		Location casa = Location.builder()
				.locationName("CASA").build();
		Location sede = Location.builder()
				.locationName("SEDE").build();
		Location trasf = Location.builder()
				.locationName("TRASFERTA").build();
		Location cliente = Location.builder()
				.locationName("CLIENTE").build();
		Location del = Location.builder()
				.locationName("DEL").build();
		locationService.save(casa);
		locationService.save(sede);
		locationService.save(trasf);
		locationService.save(cliente);
		locationService.save(del);
		assertEquals(casa, locationService.find("CASA"));
		assertEquals(sede, locationService.find("SEDE"));
		assertEquals(trasf, locationService.find("TRASFERTA"));
		assertEquals(cliente, locationService.find("CLIENTE"));
		assertEquals(del, locationService.find("DEL"));
		locationService.delete("DEL");
		assertNull(locationService.find("DEL"));
	}
}
