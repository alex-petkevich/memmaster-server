package at.abcdef.memmaster.model;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "folders")
@Getter
@Setter
@NoArgsConstructor
public class Folder
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long parentId;


	private String uuid;

	@Size(max = 255)
	private String name;

	@Size(max = 150)
	private String icon;
	
	@Size(max = 20)
	private String lng_src;

	@Size(max = 20)
	private String lng_dest;

	private Boolean active;

	@Column(name="public")
	private Boolean isPublic;

	private OffsetDateTime createdAt;

	private OffsetDateTime lastModifiedAt;

	public Folder(String name) {
		this.name = name;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(	name = "dictionary_folders",
      joinColumns = @JoinColumn(name = "dictionary_id"),
      inverseJoinColumns = @JoinColumn(name = "folder_id"))
  private List<Dictionary> dictionary = new ArrayList<>();

}
