package at.abcdef.memmaster.controllers;

import at.abcdef.memmaster.controllers.dto.request.UserRequest;
import at.abcdef.memmaster.controllers.dto.response.RoleResponse;
import at.abcdef.memmaster.controllers.mapper.RoleMapper;
import at.abcdef.memmaster.util.GlobUtil;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.abcdef.memmaster.controllers.dto.response.MessageResponse;
import at.abcdef.memmaster.controllers.dto.response.UserResponse;
import at.abcdef.memmaster.controllers.mapper.UserMapper;
import at.abcdef.memmaster.controllers.mapper.UserSignupRequestMapper;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.service.TranslateService;
import at.abcdef.memmaster.service.UserService;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
	
	private final UserService userService;
	private final UserMapper userMapper;
	private final RoleMapper roleMapper;
	private final UserSignupRequestMapper userSignupRequestMapper;

	final
	TranslateService translate;

	public UserController(UserService userService, UserMapper userMapper, RoleMapper roleMapper, UserSignupRequestMapper userSignupRequestMapper, TranslateService translate)
	{
		this.userService = userService;
		this.userMapper = userMapper;
		this.roleMapper = roleMapper;
		this.userSignupRequestMapper = userSignupRequestMapper;
		this.translate = translate;
	}

	@PostMapping("/")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<MessageResponse> saveUser(@Valid @RequestBody UserRequest userRequest) {
		User currentUserData = userService.getCurrentUser();

		if (!userRequest.getUsername().equals(currentUserData.getUsername()))
		{
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(translate.get("user.error-not-supported")));
		}
		if (!userRequest.getEmail().equals(currentUserData.getEmail()) && userService.isEmailExists(userRequest.getEmail()))
		{
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(translate.get("user.error-email-already-use")));
		}

		userRequest.setUsername(currentUserData.getUsername());
		userService.saveUser(currentUserData, userRequest, null);

		return ResponseEntity.ok(new MessageResponse(translate.get("user.saved-successfully")));
	}

	@GetMapping("/")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public UserResponse getUserInfo() {
		User user = userService.getCurrentUser();

		return userMapper.toEntity(user);
	}

	@PostMapping("/lang")
	public ResponseEntity<MessageResponse> saveUserLang(@RequestBody UserRequest userRequest) {
		User currentUserData = userService.getCurrentUser();

		if (currentUserData == null) {
			return ResponseEntity.notFound().build();
		}

		UserRequest signupRequest = userSignupRequestMapper.toEntity(currentUserData);
		signupRequest.setLang(userRequest.getLang());

		userService.saveUser(currentUserData, signupRequest, null);

		return ResponseEntity.ok(new MessageResponse(translate.get("user.lang-updated-successfully")));
	}

	@GetMapping("/admin/")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<UserResponse>> getUsers(@RequestParam(required = false) String name,
										@RequestParam(required = false)  String username,
										@RequestParam(required = false)  String email,
										@RequestParam (required = false) String role,
										@RequestParam (required = false) Boolean active,
									   @RequestParam(defaultValue = "0") int page,
									   @RequestParam(defaultValue = "createdAt-desc") String sort) {

		Pageable paging = PageRequest.of(page, 20, GlobUtil.getSortOrder(sort));

		Page<UserResponse> users = userService.getUsers(name, username, email, role, active, paging).map(userMapper::toEntity);

		return ResponseEntity.ok(users);
	}

	@PostMapping("/admin/activate/")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> activateUser(@RequestBody Long userId) {

		UserResponse user = userMapper.toEntity(userService.adminUserActivation(userId));

		return ResponseEntity.ok(user);
	}

	@GetMapping("/admin/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {

		UserResponse user = userMapper.toEntity(userService.getUser(id));

		return ResponseEntity.ok(user);
	}

	@GetMapping("/admin/roles/")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<RoleResponse>> getRoles() {

		List<RoleResponse> roles = userService.getRoles().stream().map(roleMapper::toEntity).toList();

		return ResponseEntity.ok(roles);
	}

	@PostMapping("/admin/")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<MessageResponse> saveUserAdmin(@Valid @RequestBody UserRequest userRequest) {
		User userData;
		if (userRequest.getId() != null) {
			userData = userService.getUser(userRequest.getId());
		} else {
			userData = new User();
			userData.setCreatedAt(OffsetDateTime.now());
		}

		if (!userRequest.getEmail().equals(userData.getEmail()) && userService.isEmailExists(userRequest.getEmail()))
		{
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(translate.get("user.error-email-already-use")));
		}
		if (!userRequest.getUsername().equals(userData.getUsername()) && userService.isUsernameExists(userRequest.getUsername()))
		{
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(translate.get("user.error-username-already-use")));
		}

		userService.saveUser(userData, userRequest, userRequest.getRoles());

		return ResponseEntity.ok(new MessageResponse(translate.get("user.saved-successfully")));
	}

	@DeleteMapping("/admin/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<MessageResponse> deleteUserAdmin(@PathVariable Long id) {
		this.userService.deleteUser(id);

		return ResponseEntity.ok(new MessageResponse(translate.get("user.deleted-successfully")));
	}
}
