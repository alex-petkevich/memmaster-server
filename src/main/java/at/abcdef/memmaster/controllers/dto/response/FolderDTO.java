package at.abcdef.memmaster.controllers.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FolderDTO {
    private Long id;

    private Integer user_id;

    private Long parent_id;

    private String uuid;

    private String name;

    private String icon;

    private Boolean active;

    private Boolean is_public;

    private OffsetDateTime created_at;

    private OffsetDateTime last_modified_at;
}
