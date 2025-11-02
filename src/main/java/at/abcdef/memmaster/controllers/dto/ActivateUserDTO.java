package at.abcdef.memmaster.controllers.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActivateUserDTO
{
	@NotBlank
	private String key;
}
