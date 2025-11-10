package at.abcdef.memmaster.controllers.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryPairDTO {
  private Long id;

  @Size(max = 255)
  private String name;

  @Size(max = 255)
  private String name_file;

  @Size(max = 255)
  private PairType name_type;

  @Size(max = 255)
  private String value;

  @Size(max = 255)
  private String value_file;

  @Size(max = 255)
  private PairType value_type;

  public enum PairType {
    TEXT,
    FILE
  }
}
