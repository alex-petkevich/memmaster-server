package at.abcdef.memmaster.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import at.abcdef.memmaster.config.ApplicationProperties;
import at.abcdef.memmaster.controllers.dto.UserDTO;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.repository.RoleRepository;
import at.abcdef.memmaster.repository.UserRepository;
import at.abcdef.memmaster.security.jwt.JwtUtils;
import at.abcdef.memmaster.security.oauth.OAuthService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest
{
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private JwtUtils jwtUtils;
	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private PasswordEncoder encoder;
	@Mock
	private SendMailService sendMailService;
	@Mock
	private OAuthService googleOAuthService;

	private UserService userService;

	@BeforeEach
	void setUp()
	{
		userService = new UserService(
				authenticationManager,
				jwtUtils,
				userRepository,
				roleRepository,
				encoder,
				sendMailService,
				new ApplicationProperties(),
                googleOAuthService);
	}

	@Test
	void saveUserShouldDefaultActiveToZeroWhenMissing()
	{
		User currentUser = new User();
		currentUser.setActive(null);
		UserDTO request = new UserDTO();
		request.setUsername("user");
		request.setEmail("user@example.com");
		request.setActive(null);

		userService.saveUser(currentUser, request, null);

		assertEquals(0, currentUser.getActive());
		verify(userRepository).save(currentUser);
	}

	@Test
	void saveUserShouldKeepExistingActiveWhenRequestDoesNotProvideIt()
	{
		User currentUser = new User();
		currentUser.setActive(1);
		UserDTO request = new UserDTO();
		request.setUsername("user");
		request.setEmail("user@example.com");
		request.setActive(null);

		userService.saveUser(currentUser, request, null);

		assertEquals(1, currentUser.getActive());
	}

	@Test
	void checkResetKeyShouldReturnEmptyMessageForNullActive()
	{
		User user = new User();
		user.setActive(null);
		when(userRepository.findByActivationKey("reset-key")).thenReturn(Optional.of(user));

		assertEquals("", userService.checkResetKey("reset-key").getMessage());
	}

	@Test
	void getUserShouldReturnNullWhenNotFound()
	{
		when(userRepository.findById(5)).thenReturn(Optional.empty());

		assertNull(userService.getUser(5));
	}
}

