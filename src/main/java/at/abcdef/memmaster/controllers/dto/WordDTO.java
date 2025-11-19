package at.abcdef.memmaster.controllers.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class WordDTO {
  String text;

  String translation;

  @JsonAlias("lng_src")
  String lngSource;

  @JsonAlias("lng_dest")
  String lngTarget;
}
