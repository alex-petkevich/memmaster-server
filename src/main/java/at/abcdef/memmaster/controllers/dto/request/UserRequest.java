package at.abcdef.memmaster.controllers.dto.request;

import java.util.Set;

import at.abcdef.memmaster.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest
{
	private Long id;

	@NotBlank
	@Size(min = 3, max = 20)
	private String username;

	@NotBlank
	@Size(max = 50)
	@Email
	private String email;

	@Size(max = 50)
	private String firstname;

	@Size(max = 50)
	private String lastname;

	@Size(max = 3)
	private String lang;

	private Set<Role> roles;

	private Boolean active;
}
