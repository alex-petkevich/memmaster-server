package at.abcdef.memmaster.controllers.dto.response;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import at.abcdef.memmaster.model.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse
{ 
	private Long id;

	private String username;

	private String firstname;

	private String lastname;

	private String email;

	private String lang;

	private String activationKey;

	private String image;

	private Integer active;

	private OffsetDateTime createdAt;

	private OffsetDateTime lastModifiedAt;

	private Set<Role> roles = new HashSet<>();
}
