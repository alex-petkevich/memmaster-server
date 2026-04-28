package at.abcdef.memmaster.controllers.dto.oauth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleAuthDTO {

    @NotBlank
    private String idToken;
}

