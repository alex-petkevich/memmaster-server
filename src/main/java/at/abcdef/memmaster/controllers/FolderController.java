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
	@ResponseBody
	public ResponseEntity<List<FolderDTO>> getUserFolders(@RequestParam(required = false) String name,
										@RequestParam(required = false)  String uuid,
										@RequestParam(required = false)  String parent_id,
										@RequestParam (required = false) Boolean active,
									   @RequestParam(defaultValue = "createdAt-desc") String sort)
	{
		User user = userService.getCurrentUser();
		List<FolderDTO> dbFolders = foldersService.getUserFolders(user.getId(), name, uuid, parent_id, active, sort).stream().map(folderMapper::toEntity).toList();

		List<FolderDTO> folders = new ArrayList<>();

		for (FolderDTO folder : dbFolders) {
			if (folder.getParent_id() == 0) {
				dbFolders.forEach(f -> {
					if (Objects.equals(f.getParent_id(), folder.getId())) {
						folder.getChildren().add(f);
					}
				});
				folders.add(folder);
			}
		}

		return ResponseEntity.ok(folders);
	}

	@PostMapping("/")
	public ResponseEntity<?> save(@Valid @RequestBody FolderDTO folder)
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
	public ResponseEntity<?> delete(@Valid @PathVariable Long id)
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
	public ResponseEntity<?> get(@Valid @PathVariable Long id)
	{
		User user = userService.getCurrentUser();
		Folder folder = foldersService.getUserFolder(user.getId(), id);

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
