package at.abcdef.memmaster.controllers;

import java.util.HashSet;
import java.util.Set;

import at.abcdef.memmaster.controllers.dto.request.UserRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.abcdef.memmaster.controllers.dto.request.ActivateRequest;
import at.abcdef.memmaster.controllers.dto.request.LoginRequest;
import at.abcdef.memmaster.controllers.dto.response.JwtResponse;
import at.abcdef.memmaster.controllers.dto.response.MessageResponse;
import at.abcdef.memmaster.model.ERole;
import at.abcdef.memmaster.model.Role;
import at.abcdef.memmaster.repository.RoleRepository;
import at.abcdef.memmaster.service.TranslateService;
import at.abcdef.memmaster.service.UserService;

@RestController
@RequestMapping("/api/account")
public class AccountController
{
	final
	RoleRepository roleRepository;

	final
	UserService userService;
	
	final
	TranslateService translate;

	public AccountController(RoleRepository roleRepository, UserService userService, TranslateService translate) {
		this.roleRepository = roleRepository;
		this.userService = userService;
		this.translate = translate;
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
	{
		JwtResponse response = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequest signUpRequest)
	{
		if (userService.isUsernameExists(signUpRequest.getUsername()))
		{
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(translate.get("account.register-user.username-exists")));
		}
		if (userService.isEmailExists(signUpRequest.getEmail()))
		{
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse(translate.get("account.register-user.email-exists")));
		}
		
		// Create new user's account
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException(translate.get("account.register-user.role-not-found"))));

		userService.createUser(signUpRequest, roles);
		
		return ResponseEntity.ok(new MessageResponse(translate.get("account.register-user.user-registered")));
	}

	@PostMapping("/activate")
	public ResponseEntity<JwtResponse> activateUser(@Valid @RequestBody ActivateRequest activateRequest)
	{
		JwtResponse response = userService.activate(activateRequest.getKey());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/forgot-password/{key}")
	public ResponseEntity<MessageResponse> forgotPassword(@Valid @PathVariable String key)
	{
		MessageResponse response = userService.forgotPasswordSend(key);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/password-reset")
	public ResponseEntity<?> passwordReset(@Valid @RequestBody LoginRequest loginRequest)
	{
		MessageResponse response = userService.changePassword(loginRequest.getUsername(), loginRequest.getPassword());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/check-key")
	public ResponseEntity<?> checkKey(@Valid @RequestBody ActivateRequest activateRequest)
	{
		MessageResponse response = userService.checkResetKey(activateRequest.getKey());

		return ResponseEntity.ok(response);
	}

}
