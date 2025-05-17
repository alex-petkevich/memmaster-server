package at.abcdef.memmaster.controllers.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtResponse
{
	private String accessToken;
	private String type = "Bearer";
	private Integer id;
	private String username;
	private String email;
	private String lang;
	private List<String> roles;

	public JwtResponse(String accessToken, Integer id, String username, String email, String lang, List<String> roles) {
		this.accessToken = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
		this.lang = lang;
	}
}
