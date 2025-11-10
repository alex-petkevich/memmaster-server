package at.abcdef.memmaster.controllers;

import at.abcdef.memmaster.controllers.dto.DictionaryDTO;
import at.abcdef.memmaster.controllers.dto.DictionaryPairDTO;
import at.abcdef.memmaster.controllers.mapper.DictionaryMapper;
import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.service.DictionaryService;
import at.abcdef.memmaster.service.FoldersService;
import at.abcdef.memmaster.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}
