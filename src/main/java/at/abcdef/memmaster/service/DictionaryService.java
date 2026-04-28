package at.abcdef.memmaster.service;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.repository.DictionaryRepository;
import at.abcdef.memmaster.service.dictionaryio.DictionaryExportService;
import at.abcdef.memmaster.service.dictionaryio.DictionaryImportService;
import at.abcdef.memmaster.service.dictionaryio.ExportFile;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
public class DictionaryService {

  private final DictionaryRepository dictionaryRepository;
  private final UserService userService;
  private final DictionaryImportService dictionaryImportService;
  private final DictionaryExportService dictionaryExportService;

  public DictionaryService(DictionaryRepository dictionaryRepository,
                           UserService userService,
                           DictionaryImportService dictionaryImportService,
                           DictionaryExportService dictionaryExportService) {
    this.dictionaryRepository = dictionaryRepository;
    this.userService = userService;
    this.dictionaryImportService = dictionaryImportService;
    this.dictionaryExportService = dictionaryExportService;
  }

  public List<Dictionary> getDictionaryInFolder(@Valid Folder folder) {
    return dictionaryRepository.getByFolders(Collections.singletonList(folder));
  }

  public List<Dictionary> saveDictionaryInFolder(Folder folder, List<Dictionary> dto) {

    List<Dictionary> existingRecords = dto.stream().filter(d -> d.getId() != null).toList();
    List<Long> existingIds = existingRecords.stream().map(Dictionary::getId).toList();

    dictionaryRepository.deleteAllByFoldersContainingAndIdNotIn(folder, existingIds);

    for (Dictionary d : dto) {
      if (d.getFolders() == null) {
        d.setFolders(new ArrayList<>());
      }
      if (!d.getFolders().contains(folder)) {
        d.getFolders().add(folder);
      }
      d.setUser(userService.getCurrentUser());
    }
    dictionaryRepository.saveAll(dto);
    return dto;
  }

  public List<Dictionary> bulkImportDictionary(Folder folder, List<Dictionary> newPairs) {
    Set<String> existingKeys = new HashSet<>();
    for (Dictionary existing : getDictionaryInFolder(folder)) {
      String normalized = normalizeKey(existing.getName());
      if (normalized != null) {
        existingKeys.add(normalized);
      }
    }

    List<Dictionary> toSave = new ArrayList<>();
    for (Dictionary pair : newPairs) {
      String normalized = normalizeKey(pair.getName());
      if (normalized == null || existingKeys.contains(normalized)) {
        continue;
      }
      existingKeys.add(normalized);
      prepareForFolderSave(pair, folder);
      toSave.add(pair);
    }

    return toSave.isEmpty() ? List.of() : dictionaryRepository.saveAll(toSave);
  }

  public List<Dictionary> bulkImportDictionaryFromFile(Folder folder, MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return List.of();
    }

    try {
      List<Dictionary> parsedPairs = dictionaryImportService.importFromFile(file);
      return bulkImportDictionary(folder, parsedPairs);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot parse import file", e);
    }
  }

  public List<Dictionary> copyToFolder(Folder sourceFolder, Folder targetFolder) {
    List<Dictionary> sourcePairs = getDictionaryInFolder(sourceFolder);
    return bulkImportDictionary(targetFolder, sourcePairs);
  }

  public Dictionary markAsRemembered(Long pairId) {
    Dictionary dict = dictionaryRepository.findById(pairId)
        .orElseThrow(() -> new IllegalArgumentException("Dictionary entry not found: " + pairId));
    dict.setIsRemembered(true);
    return dictionaryRepository.save(dict);
  }

  public Dictionary markAsRememberedInFolder(Folder folder, Long pairId) {
    assertPairInFolder(folder, pairId);
    return markAsRemembered(pairId);
  }

  public Dictionary markAsArchived(Long pairId) {
    Dictionary dict = dictionaryRepository.findById(pairId)
        .orElseThrow(() -> new IllegalArgumentException("Dictionary entry not found: " + pairId));
    dict.setIsArchived(true);
    return dictionaryRepository.save(dict);
  }

  public Dictionary markAsArchivedInFolder(Folder folder, Long pairId) {
    assertPairInFolder(folder, pairId);
    return markAsArchived(pairId);
  }

  public ExportFile exportDictionary(Folder folder, String format) {
    List<Dictionary> cards = getDictionaryInFolder(folder);
    String baseName = safeFileNamePart(folder.getName());

    try {
      return dictionaryExportService.export(format, baseName, cards);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to generate export", e);
    }
  }

  private String safeFileNamePart(String value) {
    String candidate = (value == null ? "" : value).trim();
    if (candidate.isEmpty()) {
      return "dictionary";
    }
    return candidate.replaceAll("[^a-zA-Z0-9._-]", "_");
  }


  private void prepareForFolderSave(Dictionary pair, Folder folder) {
    if (pair.getFolders() == null) {
      pair.setFolders(new ArrayList<>());
    }
    if (!pair.getFolders().contains(folder)) {
      pair.getFolders().add(folder);
    }
    pair.setUser(userService.getCurrentUser());
  }

  private String normalizeKey(String key) {
    if (key == null) {
      return null;
    }
    String normalized = key.trim().toLowerCase(Locale.ROOT);
    return normalized.isEmpty() ? null : normalized;
  }

  private void assertPairInFolder(Folder folder, Long pairId) {
    if (!dictionaryRepository.existsByIdAndFoldersContaining(pairId, folder)) {
      throw new IllegalArgumentException("Dictionary entry not found in folder: " + pairId);
    }
  }
}
