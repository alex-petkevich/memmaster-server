package at.abcdef.memmaster.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "directory")
@Getter
@Setter
@NoArgsConstructor
public class Directory {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   @Column(name = "type", nullable = false)
   private String type;

   @Column(name = "key", nullable = false)
   private String key;

   @Column(name = "value", nullable = false)
   private String value;
}

