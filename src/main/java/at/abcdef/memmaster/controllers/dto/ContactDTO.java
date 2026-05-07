package at.abcdef.memmaster.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactDTO {

    @NotBlank
    private String questionType;

    @NotBlank
    private String subject;

    @NotBlank
    private String comment;
}

