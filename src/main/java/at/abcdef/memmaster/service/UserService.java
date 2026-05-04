package at.abcdef.memmaster.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import at.abcdef.memmaster.controllers.dto.UserDTO;
import at.abcdef.memmaster.model.specification.UserSpecification;
import at.abcdef.memmaster.repository.RoleRepository;
import at.abcdef.memmaster.security.oauth.OAuthService;
import at.abcdef.memmaster.security.oauth.OAuthUserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import at.abcdef.memmaster.config.ApplicationProperties;
import at.abcdef.memmaster.controllers.dto.oauth.JwtDTO;
import at.abcdef.memmaster.controllers.dto.MessageResponseDTO;
import at.abcdef.memmaster.model.Role;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.repository.UserRepository;
import at.abcdef.memmaster.security.jwt.JwtUtils;
import at.abcdef.memmaster.security.services.UserDetailsImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class UserService
{
	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	public static final String SUCCESSFUL = "successful";
	final AuthenticationManager authenticationManager;
	final JwtUtils jwtUtils;
	final UserRepository userRepository;
	final RoleRepository roleRepository;
	final PasswordEncoder encoder;
	private final SendMailService sendMailService;
	private final ApplicationProperties applicationProperties;
	private final OAuthService googleOAuthService;

	public UserService(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository,
					   RoleRepository roleRepository, PasswordEncoder encoder, SendMailService sendMailService,
					   ApplicationProperties applicationProperties, OAuthService googleOAuthService)
	{
		this.authenticationManager = authenticationManager;
		this.jwtUtils = jwtUtils;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.encoder = encoder;
		this.sendMailService = sendMailService;
		this.applicationProperties = applicationProperties;
		this.googleOAuthService = googleOAuthService;
	}

	public JwtDTO authenticate(String username, String password)
	{
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, password));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList();

		return new JwtDTO(jwt,
				userDetails.getId(),
				userDetails.getUsername(),
				userDetails.getEmail(),
				userDetails.getLang(),
				roles);
	}

	public JwtDTO authenticateWithGoogle(String idToken)
	{
		OAuthUserInfo userInfo = googleOAuthService.verifyAndExtractUserInfo(idToken);

		String configuredClientId = applicationProperties.getOauth().getGoogleClientId();
		if (!StringUtils.hasText(configuredClientId)) {
			throw new IllegalStateException("Google OAuth is not configured.");
		}
		if (!userInfo.emailVerified()) {
			throw new IllegalArgumentException("Google account email is not verified.");
		}

		User user = userRepository.findByEmail(userInfo.email())
				.orElseGet(() -> createOAuthUser(userInfo));

		if (!Integer.valueOf(1).equals(user.getActive())) {
			user.setActive(1);
			user.setActivationKey("");
			user.setLastModifiedAt(OffsetDateTime.now());
			user = userRepository.save(user);
		}

		return buildJwtForUser(user);
	}

	private User createOAuthUser(OAuthUserInfo userInfo)
	{
		Role userRole = roleRepository.findByName(at.abcdef.memmaster.model.ERole.ROLE_USER)
				.orElseThrow(() -> new IllegalStateException("ROLE_USER not found"));

		User user = new User();
		user.setEmail(userInfo.email());
		user.setUsername(generateUniqueUsername(userInfo.email()));
		user.setFirstname(userInfo.firstName());
		user.setLastname(userInfo.lastName());
		user.setActive(1);
		user.setActivationKey("");
		user.setLang(applicationProperties.getGeneral().getDefaultLang());
		user.setCreatedAt(OffsetDateTime.now());
		user.setLastModifiedAt(OffsetDateTime.now());
		user.setRoles(Set.of(userRole));
		return userRepository.save(user);
	}

	private String generateUniqueUsername(String email)
	{
		String localPart = email.split("@")[0].replaceAll("[^A-Za-z0-9._-]", "");
		String base = StringUtils.hasText(localPart) ? localPart : "user";
		if (base.length() > 30) {
			base = base.substring(0, 30);
		}

		String username = base;
		int index = 1;
		while (userRepository.existsByUsername(username)) {
			String suffix = String.valueOf(index++);
			int maxBaseLen = Math.max(1, 30 - suffix.length());
			String trimmedBase = base.length() > maxBaseLen ? base.substring(0, maxBaseLen) : base;
			username = trimmedBase + suffix;
		}
		return username;
	}


	private JwtDTO buildJwtForUser(User user)
	{
		UserDetailsImpl userDetails = UserDetailsImpl.build(user);
		Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);

		List<String> roles = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.toList();

		return new JwtDTO(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), userDetails.getLang(), roles);
	}


	public boolean isUsernameExists(String username)
	{
		return userRepository.existsByUsername(username);
	}

	public boolean isEmailExists(String email)
	{
		return userRepository.existsByEmail(email);
	}

	public void createUser(UserDTO signUpRequest, Set<Role> roles)
	{
		User user = new User();
		user.setUsername(signUpRequest.getUsername());
		user.setEmail(signUpRequest.getEmail());
		user.setFirstname(signUpRequest.getFirstname());
		user.setLastname(signUpRequest.getLastname());
		user.setActive(0);
		user.setCreatedAt(OffsetDateTime.now());
		user.setLastModifiedAt(OffsetDateTime.now());
		user.setLang(applicationProperties.getGeneral().getDefaultLang());
		user.setActivationKey(generateActivationKey());	
		if (roles != null)
		{
			user.setRoles(roles);
		}
		userRepository.save(user);
		
		sendMailService.sendActivationEmail(user);
	}

	public void saveUser(User currentUserData, UserDTO signUpRequest, Set<Role> roles)
	{
		currentUserData.setUsername(signUpRequest.getUsername());
		currentUserData.setEmail(signUpRequest.getEmail());
		currentUserData.setFirstname(signUpRequest.getFirstname());
		currentUserData.setLastname(signUpRequest.getLastname());
		currentUserData.setLastModifiedAt(OffsetDateTime.now());
		if (signUpRequest.getActive() != null)
		{
			currentUserData.setActive(signUpRequest.getActive());
		}
		else if (currentUserData.getActive() == null)
		{
			currentUserData.setActive(0);
		}
		if (StringUtils.hasText(signUpRequest.getLang()))
		{
			currentUserData.setLang(signUpRequest.getLang());
		}
		if (roles != null)
		{
			currentUserData.setRoles(roles);
		}
		userRepository.save(currentUserData);
	}

	private String generateActivationKey()
	{
		return UUID.randomUUID().toString();
	}

	public JwtDTO activate(String key)
	{
		User user = userRepository.findByActivationKey(key).orElse(null);
		if (user != null) {
			user.setActivationKey("");
			user.setActive(1);
			user.setLastModifiedAt(OffsetDateTime.now());
			userRepository.save(user);

			this.forgotPasswordSend(user.getEmail());

			return new JwtDTO(null, user.getId(), user.getUsername(), user.getEmail(), user.getLang(), null);
		}
		
		return new JwtDTO(null, null, null, null, null, null);
	}

	public MessageResponseDTO forgotPasswordSend(String key)
	{
		User user = userRepository.findForResetPassword(key).orElse(null);
		if (user != null) {
			user.setActivationKey(generateActivationKey());
			user.setLastModifiedAt(OffsetDateTime.now());
			userRepository.save(user);

			sendMailService.sendPasswordResetMail(user);
			return new MessageResponseDTO(SUCCESSFUL);
		}

		return new MessageResponseDTO("");
	}

	public MessageResponseDTO checkResetKey(String key)
	{
		User user = userRepository.findByActivationKey(key).orElse(null);
		if (user != null && Integer.valueOf(1).equals(user.getActive())) {
			return new MessageResponseDTO(SUCCESSFUL);
		}
		return new MessageResponseDTO("");
	}

	public MessageResponseDTO changePassword(String username, String password)
	{
		User user = userRepository.findByActivationKey(username).orElse(null);
		if (user != null) {
			user.setPassword(encoder.encode(password));
			user.setActivationKey("");
			user.setLastModifiedAt(OffsetDateTime.now());
			userRepository.save(user);
			
			return new MessageResponseDTO(SUCCESSFUL);
		}
		
		return new MessageResponseDTO("");
	}

	public User getCurrentUser()
	{
		if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
			return null;
		}
		
		UserDetailsImpl printcipal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		return userRepository.findByUsername(printcipal.getUsername()).orElse(null);
	}

	public Page<User> getUsers(String name, String username, String email, String role, Boolean active, Pageable pageable)
	{
		Specification<User> spec = (root, query, cb) -> cb.conjunction();

		if (StringUtils.hasText(username)) {
			spec = spec.and(UserSpecification.hasUsername(username));
		}
		if (StringUtils.hasText(name)) {
			spec = spec.and(UserSpecification.hasName(name));
		}
		if (StringUtils.hasText(email)) {
			spec = spec.and(UserSpecification.hasEmail(email));
		}
		if (StringUtils.hasText(role)) {
			spec = spec.and(UserSpecification.hasRole(role));
		}
		if (Boolean.TRUE.equals(active)) {
			spec = spec.and(UserSpecification.hasActive(active));
		}

		return userRepository.findAll(spec, pageable);
	}

	public User adminUserActivation(Integer userId) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isEmpty()) {
			return null;
		}

		User saveUser = user.get();
		saveUser.setActive(saveUser.getActive() != null && saveUser.getActive() == 0 ? 1 : 0);
		return userRepository.save(saveUser);
	}

	public User getUser(Integer userId)
	{
		return userRepository.findById(userId).orElse(null);
	}

	public List<Role> getRoles()
	{
		return roleRepository.findAll();
	}

	public void deleteUser(Integer id) {
		if (id != null) {
			userRepository.deleteById(id);
		}
	}
}
