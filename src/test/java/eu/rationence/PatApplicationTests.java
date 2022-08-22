package eu.rationence;

import eu.rationence.pat.PatApplication;
import eu.rationence.pat.model.Role;
import eu.rationence.pat.service.RoleService;
import eu.rationence.pat.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(classes = PatApplication.class)
class PatApplicationTests {
	private final RoleService roleService;
	@Autowired
	PatApplicationTests(RoleService roleService) {
		this.roleService = roleService;
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
		assertEquals(roleService.findRole("ADMIN").getRoleName(), "ADMIN");
		assertEquals(roleService.findRole("USER").getRoleName(), "USER");
		assertEquals(roleService.findRole("ROLE_TO_DELETE").getRoleName(), "ROLE_TO_DELETE");
		roleService.deleteRole("ROLE_TO_DELETE");
		assertNull(roleService.findRole("ROLE_TO_DELETE"));
	}

}
