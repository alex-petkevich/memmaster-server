package at.abcdef.memmaster.controllers.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DictionaryDTO {
  private Long id;

  @NotNull
  private Integer user_id;

  @Size(max = 255)
  private String name;

  @Size(max = 255)
  private String name_img;

  @Size(max = 255)
  private String value;

  @Size(max = 255)
  private String value_img;

  private Boolean is_remembered;

  private Boolean is_archived;

  private Instant created_at;

  private Instant last_modified_at;

  private List<FolderDTO> folders;

}
