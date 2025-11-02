package at.abcdef.memmaster.controllers.dto;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import at.abcdef.memmaster.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO
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

    private String image;

	private Set<Role> roles = new HashSet<>();

	private Integer active;

    private String activationKey;

    private OffsetDateTime createdAt;

    private OffsetDateTime lastModifiedAt;

}
