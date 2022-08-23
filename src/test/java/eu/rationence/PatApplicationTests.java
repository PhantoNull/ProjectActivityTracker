package eu.rationence;

import eu.rationence.pat.PatApplication;
import eu.rationence.pat.model.Client;
import eu.rationence.pat.model.ClientType;
import eu.rationence.pat.model.Role;
import eu.rationence.pat.service.ClientService;
import eu.rationence.pat.service.ClientTypeService;
import eu.rationence.pat.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = PatApplication.class)
class PatApplicationTests {
	private final RoleService roleService;
	private final ClientTypeService clientTypeService;
	private final ClientService clientService;
	@Autowired
	PatApplicationTests(RoleService roleService, ClientTypeService clientTypeService, ClientService clientService) {
		this.roleService = roleService;
		this.clientTypeService = clientTypeService;
		this.clientService = clientService;
	}

	@Test
	void rolesTest() {
		assertNull(roleService.findRole("ADMIN"));
		assertNull(roleService.findRole("USER"));
		assertNull(roleService.findRole("ROLE_TO_DELETE"));
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
		assertEquals("ADMIN", roleService.findRole("ADMIN").getRoleName());
		assertEquals("USER", roleService.findRole("USER").getRoleName());
		assertEquals("ROLE_TO_DELETE", roleService.findRole("ROLE_TO_DELETE").getRoleName());
		roleService.deleteRole("ROLE_TO_DELETE");
		assertNull(roleService.findRole("ROLE_TO_DELETE"));
	}

	@Test
	void clientTypeTest(){
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
	void clientsTest(){
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

}
