package at.abcdef.memmaster.security.services;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Set;

import org.junit.jupiter.api.Test;

import at.abcdef.memmaster.model.ERole;
import at.abcdef.memmaster.model.Role;
import at.abcdef.memmaster.model.User;

class UserDetailsImplTest
{
	@Test
	void buildShouldTreatNullActiveAsDisabled()
	{
		User user = new User();
		user.setId(1);
		user.setUsername("user");
		user.setEmail("user@example.com");
		user.setPassword("secret");
		user.setLang("en");
		user.setActive(null);
		user.setRoles(Set.of(new Role(ERole.ROLE_USER)));

		UserDetailsImpl userDetails = UserDetailsImpl.build(user);

		assertFalse(userDetails.isEnabled());
		assertFalse(userDetails.isAccountNonLocked());
	}
}

