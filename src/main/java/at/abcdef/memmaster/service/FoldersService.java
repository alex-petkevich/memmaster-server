package at.abcdef.memmaster.service;

import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.repository.FolderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
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

		return folderRepository.save(folder);

	}

	public List<Folder> getUserFolders(Integer userId)
	{

		return folderRepository.getByUserId(userId);
	}

	public Folder getUserFolder(Integer userId, Long folderId) {
		return folderRepository.getReferenceById(id);
	}

	public void deleteUserFolder(Integer userId, Long folderId) {
		if (folderId != null) {
			folderRepository.deleteById(folderId);
		}
	}

}
