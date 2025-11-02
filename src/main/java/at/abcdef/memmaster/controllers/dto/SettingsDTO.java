package at.abcdef.memmaster.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
public class SettingsDTO {
    private Long id;

    private Integer user_id;

    private String name;

    private String value;

    private OffsetDateTime createdAt;

    private OffsetDateTime lastModifiedAt;
}
