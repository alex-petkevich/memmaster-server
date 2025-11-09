package at.abcdef.memmaster.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "dictionary")
@NoArgsConstructor
public class Dictionary {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Size(max = 255)
  @Column(name = "name")
  private String name;

  @Size(max = 255)
  @Column(name = "name_img")
  private String nameImg;

  @Size(max = 255)
  @Column(name = "value")
  private String value;

  @Size(max = 255)
  @Column(name = "value_img")
  private String valueImg;

  @Column(name = "is_remembered")
  private Boolean isRemembered;

  @Column(name = "is_archived")
  private Boolean isArchived;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "last_modified_at")
  private Instant lastModifiedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(	name = "dictionary_folders",
      joinColumns = @JoinColumn(name = "dictionary_id"),
      inverseJoinColumns = @JoinColumn(name = "folder_id"))
  private List<Folder> folders = new ArrayList<>();

}