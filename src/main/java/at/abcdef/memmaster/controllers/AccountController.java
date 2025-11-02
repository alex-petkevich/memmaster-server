package at.abcdef.memmaster.controllers;

import java.util.HashSet;
import java.util.Set;

import at.abcdef.memmaster.controllers.dto.*;
import at.abcdef.memmaster.controllers.dto.UserDTO;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
	public ResponseEntity<JwtDTO> authenticateUser(@Valid @RequestBody LoginDTO loginRequest)
	{
		JwtDTO response = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/signup")
	public ResponseEntity<MessageResponseDTO> registerUser(@Valid @RequestBody UserDTO signUpRequest)
	{
		if (userService.isUsernameExists(signUpRequest.getUsername()))
		{
			return ResponseEntity
					.badRequest()
					.body(new MessageResponseDTO(translate.get("account.register-user.username-exists")));
		}
		if (userService.isEmailExists(signUpRequest.getEmail()))
		{
			return ResponseEntity
					.badRequest()
					.body(new MessageResponseDTO(translate.get("account.register-user.email-exists")));
		}
		
		// Create new user's account
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow(() -> new RuntimeException(translate.get("account.register-user.role-not-found"))));

		userService.createUser(signUpRequest, roles);
		
		return ResponseEntity.ok(new MessageResponseDTO(translate.get("account.register-user.user-registered")));
	}

	@PostMapping("/activate")
	public ResponseEntity<JwtDTO> activateUser(@Valid @RequestBody ActivateUserDTO activateUserDTO)
	{
		JwtDTO response = userService.activate(activateUserDTO.getKey());

		return ResponseEntity.ok(response);
	}

	@GetMapping("/forgot-password/{key}")
	public ResponseEntity<MessageResponseDTO> forgotPassword(@Valid @PathVariable String key)
	{
		MessageResponseDTO response = userService.forgotPasswordSend(key);

		return ResponseEntity.ok(response);
	}

	@PostMapping("/password-reset")
	public ResponseEntity<MessageResponseDTO> passwordReset(@Valid @RequestBody LoginDTO loginRequest)
	{
		MessageResponseDTO response = userService.changePassword(loginRequest.getUsername(), loginRequest.getPassword());

		return ResponseEntity.ok(response);
	}

	@PostMapping("/check-key")
	public ResponseEntity<MessageResponseDTO> checkKey(@Valid @RequestBody ActivateUserDTO activateUserDTO)
	{
		MessageResponseDTO response = userService.checkResetKey(activateUserDTO.getKey());

		return ResponseEntity.ok(response);
	}

}
