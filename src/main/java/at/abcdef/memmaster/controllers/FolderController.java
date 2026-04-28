package at.abcdef.memmaster.controllers;

import at.abcdef.memmaster.controllers.dto.FolderDTO;
import at.abcdef.memmaster.controllers.mapper.FolderMapper;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.service.FoldersService;
import at.abcdef.memmaster.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/folders")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class FolderController
{
	final
	UserService userService;

	final
	FoldersService foldersService;

	final
	FolderMapper folderMapper;

	public FolderController(UserService userService, FoldersService foldersService, FolderMapper folderMapper) {
		this.userService = userService;
		this.foldersService = foldersService;
		this.folderMapper = folderMapper;
	}

	@GetMapping("/")
	public ResponseEntity<List<FolderDTO>> getUserFolders(@RequestParam(required = false) String name,
										@RequestParam(required = false)  String uuid,
										@RequestParam(required = false)  String parent_id,
										@RequestParam (required = false) Boolean active,
									   @RequestParam(defaultValue = "createdAt-desc") String sort)
	{
		User user = userService.getCurrentUser();
		List<FolderDTO> dbFolders = foldersService.getUserFolders(user.getId(), name, uuid, parent_id, active, sort).stream().map(folderMapper::toEntity).toList();

		return ResponseEntity.ok(buildFolderTree(dbFolders));
	}

	@GetMapping("/mine")
	public ResponseEntity<List<FolderDTO>> getMyFolders(@RequestParam(required = false) String name,
										@RequestParam(required = false)  String uuid,
										@RequestParam(required = false)  String parent_id,
										@RequestParam (required = false) Boolean active,
									   @RequestParam(defaultValue = "createdAt-desc") String sort)
	{
		User user = userService.getCurrentUser();
		List<FolderDTO> dbFolders = foldersService.getUserFolders(user.getId(), name, uuid, parent_id, active, sort)
				.stream()
				.filter(f -> Objects.equals(f.getUser().getId(), user.getId()))
				.map(folderMapper::toEntity)
				.toList();

		return ResponseEntity.ok(buildFolderTree(dbFolders));
	}

	@GetMapping("/shared/{uuid}")
	public ResponseEntity<FolderDTO> getSharedFolderByUuid(@PathVariable String uuid)
	{
		Folder folder = foldersService.getPublicFolderByUuid(uuid);
		if (folder == null) {
			return ResponseEntity.notFound().build();
		}

		FolderDTO folderDTO = folderMapper.toEntity(folder);
		folderDTO.setDictionary_count(foldersService.getFolderDictionarySize(folderDTO.getId()));
		folderDTO.setAvailable_dictionary_count(foldersService.getFolderLearnableDictionarySize(folderDTO.getId()));
		folderDTO.setUnarchived_dictionary_count(foldersService.getFolderUnarchivedDictionarySize(folderDTO.getId()));
		return ResponseEntity.ok(folderDTO);
	}

	private List<FolderDTO> buildFolderTree(List<FolderDTO> dbFolders) {

		List<FolderDTO> folders = new ArrayList<>();

		for (FolderDTO folder : dbFolders) {
			if (folder.getParent_id() == 0) {
				dbFolders.forEach(f -> {
					if (Objects.equals(f.getParent_id(), folder.getId())) {
            f.setDictionary_count(foldersService.getFolderDictionarySize(f.getId()));
            f.setAvailable_dictionary_count(foldersService.getFolderLearnableDictionarySize(f.getId()));
            f.setUnarchived_dictionary_count(foldersService.getFolderUnarchivedDictionarySize(f.getId()));
						folder.getChildren().add(f);
					}
				});
        folder.setDictionary_count(foldersService.getFolderDictionarySize(folder.getId()));
        folder.setAvailable_dictionary_count(foldersService.getFolderLearnableDictionarySize(folder.getId()));
        folder.setUnarchived_dictionary_count(foldersService.getFolderUnarchivedDictionarySize(folder.getId()));
				folders.add(folder);
			}
		}

		return folders;
	}

	@PostMapping("/")
	public ResponseEntity<String> save(@Valid @RequestBody FolderDTO folder)
	{
		User user = userService.getCurrentUser();

		if (folder.getId() != null) {
			Folder existingFolder = foldersService.getUserFolder(user.getId(), folder.getId());
			if (existingFolder == null) {
				return ResponseEntity.status(403).body("You do not have permission to edit this folder.");
			}
		}

		foldersService.saveFolder(user, folderMapper.toDto(folder));

		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> delete(@Valid @PathVariable Long id)
	{
		User user = userService.getCurrentUser();

		Folder existingFolder = foldersService.getUserFolder(user.getId(), id);
		if (existingFolder == null) {
			return ResponseEntity.status(403).body("You do not have permission to delete this folder.");
		}

		foldersService.deleteUserFolder(user.getId(), id);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<FolderDTO> get(@Valid @PathVariable Long id)
	{
		User user = userService.getCurrentUser();
		Folder folder = foldersService.getUserOrPublicFolder(user.getId(), id);
		if (folder == null) {
			return ResponseEntity.status(403).build();
		}

		return ResponseEntity.ok(folderMapper.toEntity(folder));
	}

	@PostMapping("/activate/")
	public ResponseEntity<?> activateUser(@RequestBody Long folderId) {

		User user = userService.getCurrentUser();

		Folder existingFolder = foldersService.getUserFolder(user.getId(), folderId);
		if (existingFolder == null) {
			return ResponseEntity.status(403).body("You do not have permission to change this folder.");
		}

		FolderDTO folder = folderMapper.toEntity(foldersService.folderActivation(folderId));

		return ResponseEntity.ok(folder);
	}

}
