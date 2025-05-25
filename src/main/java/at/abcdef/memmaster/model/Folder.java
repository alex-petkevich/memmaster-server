package at.abcdef.memmaster.model;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

}
