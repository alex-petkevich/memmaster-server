package at.abcdef.memmaster.controllers;

import at.abcdef.memmaster.config.ApplicationProperties;
import at.abcdef.memmaster.controllers.dto.response.SettingsResponse;
import at.abcdef.memmaster.controllers.mapper.SettingsMapper;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.service.FoldersService;
import at.abcdef.memmaster.service.SettingsService;
import at.abcdef.memmaster.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

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

	public FolderController(UserService userService, FoldersService foldersService, ApplicationProperties applicationProperties, FolderMapper folderMapper) {
		this.userService = userService;
		this.foldersService = foldersService;
		this.applicationProperties = applicationProperties;
		this.folderMapper = folderMapper;
	}

	@GetMapping("/")
	@ResponseBody
	public ResponseEntity<List<SettingsResponse>> getUserFolders()
	{
		User user = userService.getCurrentUser();
		List<SettingsResponse> result = foldersService.getUserFolders(user.getId()).stream().map(folderMapper::toEntity).toList();

		return ResponseEntity.ok(result);
	}

	@PostMapping("/")
	public ResponseEntity<?> save(@Valid @RequestBody Map<String, String> userSettings)
	{
		User user = userService.getCurrentUser();
		settingsService.saveSettings(user, userSettings);

		return ResponseEntity.ok().build();
	}

}
