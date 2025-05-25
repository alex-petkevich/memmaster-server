package at.abcdef.memmaster.service;

import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.model.specification.FolderSpecification;
import at.abcdef.memmaster.repository.FolderRepository;

import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FoldersService
{
	private final FolderRepository folderRepository;

	public FoldersService(FolderRepository folderRepository) {
		this.folderRepository = folderRepository;
	}

	public Folder saveFolder(User user, Folder folder)
	{
		folder.setUser(user);
		if (folder.getId() == null) {
			folder.setUuid(UUID.randomUUID().toString());
		}
		if (folder.getParentId() == null) {
			folder.setParentId(0L);
		}
		folder.setLastModifiedAt(OffsetDateTime.now());
		if (folder.getCreatedAt() == null) {
			folder.setCreatedAt(OffsetDateTime.now());
		}

		return folderRepository.save(folder);

	}

	public List<Folder> getUserFolders(Integer userId , String name, String uuid, String parentId, Boolean active, String sort)
	{
		Specification<Folder> spec = Specification.where(null);

		if (name != null && !name.isEmpty()) {
			spec = spec.and(FolderSpecification.hasName(name.toLowerCase()));
		}
		if (uuid != null && !uuid.isEmpty()) {
			spec = spec.and(FolderSpecification.hasUuid(uuid.toLowerCase()));
		}
		if (parentId != null && !parentId.isEmpty()) {
			spec = spec.and(FolderSpecification.hasParentId(Long.valueOf(parentId)));
		}
		if (Boolean.TRUE.equals(active)) {
			spec = spec.and(FolderSpecification.hasActive(active));
		}
				
		if (sort == null || sort.isEmpty()) {
			sort = "createdAt-desc"; // Default sort order
		}

		Sort sortOder = Sort.by(Sort.Direction.DESC, "createdAt");
		if (StringUtils.hasText(sort) && !sort.contains("-")) {
			sort = sort + "-asc"; 
		}
		if (sort.contains("name")) {
			sortOder = Sort.by(Sort.Direction.fromString(sort.split("-")[1]), "name");
		} else if (sort.contains("createdAt")) {
			sortOder = Sort.by(Sort.Direction.fromString(sort.split("-")[1]), "createdAt");
		} else if (sort.contains("lastModifiedAt")) {
			sortOder = Sort.by(Sort.Direction.fromString(sort.split("-")[1]), "lastModifiedAt");
		} else if (sort.contains("id")) {
			sortOder = Sort.by(Sort.Direction.fromString(sort.split("-")[1]), "id");
		} else if (sort.contains("uuid")) {
			sortOder = Sort.by(Sort.Direction.fromString(sort.split("-")[1]), "uuid");
		} else if (sort.contains("parentId")) {
			sortOder = Sort.by(Sort.Direction.fromString(sort.split("-")[1]), "parentId");
		} else if (sort.contains("active")) {
			sortOder = Sort.by(Sort.Direction.fromString(sort.split("-")[1]), "active");
		}
		
		if (sort.contains("asc")) {
			sortOder = sortOder.ascending();
		} else {
			sortOder = sortOder.descending();
		}


		return folderRepository.findAll(spec, sortOder);

	}

	public Folder getUserFolder(Integer userId, Long folderId) {
		Folder folder = folderRepository.getReferenceById(folderId);
		if (!Objects.equals(folder.getUser().getId(), userId)) {
			return null; 
		}
		return folder;
	}

	public void deleteUserFolder(Integer userId, Long folderId) {
		if (folderId != null) {
			folderRepository.deleteById(folderId);
		}
	}

	public Folder folderActivation(Long folderId) {
		Folder folder = folderRepository.getReferenceById(folderId);
		if (folder != null) {
			folder.setActive(!folder.getActive());
			folder.setLastModifiedAt(OffsetDateTime.now());
			return folderRepository.save(folder);
		}
		return null;
	}

}
