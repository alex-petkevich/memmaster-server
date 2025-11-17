package at.abcdef.memmaster.controllers.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class WordDTO {
  String text;
  String lng_src;
  String lng_dest;
}
