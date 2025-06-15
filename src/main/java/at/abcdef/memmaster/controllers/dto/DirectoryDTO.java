package at.abcdef.memmaster.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DirectoryDTO {

   private Long id;
   private String type;
   private String key;
   private String value;
}
