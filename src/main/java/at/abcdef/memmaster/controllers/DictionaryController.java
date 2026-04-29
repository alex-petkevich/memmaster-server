package at.abcdef.memmaster.controllers;

import at.abcdef.memmaster.controllers.dto.DictionaryPairDTO;
import at.abcdef.memmaster.controllers.mapper.DictionaryMapper;
import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.service.DictionaryService;
import at.abcdef.memmaster.service.FoldersService;
import at.abcdef.memmaster.service.UserService;
import at.abcdef.memmaster.service.dictionaryio.ExportFile;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/dictionary")
public class DictionaryController {

  final DictionaryService dictionaryService;

  final FoldersService foldersService;

  final UserService userService;

  final DictionaryMapper dictionaryMapper;

  public DictionaryController(DictionaryService dictionaryService, FoldersService foldersService, UserService userService, DictionaryMapper dictionaryMapper) {
    this.dictionaryService = dictionaryService;
    this.foldersService = foldersService;
    this.userService = userService;
    this.dictionaryMapper = dictionaryMapper;
  }

  @GetMapping("/{folderId}")
  public ResponseEntity<?> list(@Valid @PathVariable Long folderId) {
    User user = userService.getCurrentUser();

    Folder existingFolder = foldersService.getUserOrPublicFolder(user.getId(), folderId);
    if (existingFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to edit this folder.");
    }
    List<Dictionary> dictionary = dictionaryService.getDictionaryInFolder(existingFolder);

    return ResponseEntity.ok(dictionaryMapper.toPairDTO(dictionary));
  }

  @PostMapping("/{folderId}")
  public ResponseEntity<?> save(@Valid @PathVariable Long folderId, @RequestBody List<DictionaryPairDTO> dictionaryPairs) {
    User user = userService.getCurrentUser();

    Folder existingFolder = foldersService.getUserFolder(user.getId(), folderId);
    if (existingFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to edit this folder.");
    }

    List<Dictionary> dictionary = dictionaryService.saveDictionaryInFolder(existingFolder, dictionaryMapper.fromPairDto(dictionaryPairs));

    return ResponseEntity.ok(dictionaryMapper.toPairDTO(dictionary));
  }

  @PostMapping("/{folderId}/bulk-import")
  public ResponseEntity<?> bulkImport(@Valid @PathVariable Long folderId, @RequestBody List<DictionaryPairDTO> dictionaryPairs) {
    User user = userService.getCurrentUser();

    Folder existingFolder = foldersService.getUserFolder(user.getId(), folderId);
    if (existingFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to edit this folder.");
    }

    List<Dictionary> savedDictionary = dictionaryService.bulkImportDictionary(existingFolder, dictionaryMapper.fromPairDto(dictionaryPairs));

    return ResponseEntity.ok(dictionaryMapper.toPairDTO(savedDictionary));
  }

  @PostMapping("/{folderId}/bulk-import-file")
  public ResponseEntity<?> bulkImportFile(@Valid @PathVariable Long folderId, @RequestParam("file") MultipartFile file) {
    User user = userService.getCurrentUser();

    Folder existingFolder = foldersService.getUserFolder(user.getId(), folderId);
    if (existingFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to edit this folder.");
    }

    List<Dictionary> savedDictionary = dictionaryService.bulkImportDictionaryFromFile(existingFolder, file);
    return ResponseEntity.ok(dictionaryMapper.toPairDTO(savedDictionary));
  }

  @PatchMapping("/{folderId}/{pairId}/remembered")
  public ResponseEntity<?> markAsRemembered(@Valid @PathVariable Long folderId, @Valid @PathVariable Long pairId) {
    User user = userService.getCurrentUser();

    Folder existingFolder = foldersService.getUserFolder(user.getId(), folderId);
    if (existingFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to edit this folder.");
    }

    Dictionary updated = dictionaryService.markAsRememberedInFolder(existingFolder, pairId);
    return ResponseEntity.ok(dictionaryMapper.toPairDTO(updated));
  }

  @PatchMapping("/{folderId}/{pairId}/archived")
  public ResponseEntity<?> markAsArchived(@Valid @PathVariable Long folderId, @Valid @PathVariable Long pairId) {
    User user = userService.getCurrentUser();

    Folder existingFolder = foldersService.getUserFolder(user.getId(), folderId);
    if (existingFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to edit this folder.");
    }

    Dictionary updated = dictionaryService.markAsArchivedInFolder(existingFolder, pairId);
    return ResponseEntity.ok(dictionaryMapper.toPairDTO(updated));
  }

  @PatchMapping("/{folderId}/{pairId}/toggle-archived")
  public ResponseEntity<?> toggleArchived(@Valid @PathVariable Long folderId, @Valid @PathVariable Long pairId) {
    User user = userService.getCurrentUser();

    Folder existingFolder = foldersService.getUserFolder(user.getId(), folderId);
    if (existingFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to edit this folder.");
    }

    Dictionary updated = dictionaryService.toggleArchivedInFolder(existingFolder, pairId);
    return ResponseEntity.ok(dictionaryMapper.toPairDTO(updated));
  }

  @PostMapping("/{sourceFolderId}/copy-to/{targetFolderId}")
  public ResponseEntity<?> copyToFolder(@Valid @PathVariable Long sourceFolderId,
                                        @Valid @PathVariable Long targetFolderId) {
    User user = userService.getCurrentUser();

    Folder sourceFolder = foldersService.getUserOrPublicFolder(user.getId(), sourceFolderId);
    if (sourceFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to read this folder.");
    }

    Folder targetFolder = foldersService.getUserFolder(user.getId(), targetFolderId);
    if (targetFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to write to this folder.");
    }

    List<Dictionary> copied = dictionaryService.copyToFolder(sourceFolder, targetFolder);
    return ResponseEntity.ok(dictionaryMapper.toPairDTO(copied));
  }

  @GetMapping("/{folderId}/export")
  public ResponseEntity<?> export(@Valid @PathVariable Long folderId, @RequestParam(defaultValue = "csv") String format) {
    User user = userService.getCurrentUser();

    Folder existingFolder = foldersService.getUserOrPublicFolder(user.getId(), folderId);
    if (existingFolder == null) {
      return ResponseEntity.status(403).body("You do not have permission to view this folder.");
    }

    ExportFile export = dictionaryService.exportDictionary(existingFolder, format);
    return ResponseEntity.ok()
      .header(HttpHeaders.CONTENT_DISPOSITION,
        "attachment; filename*=UTF-8''" + java.net.URLEncoder.encode(export.filename(), StandardCharsets.UTF_8).replace("+", "%20"))
      .header(HttpHeaders.CONTENT_TYPE, export.contentType())
      .body(export.content());
  }
}
