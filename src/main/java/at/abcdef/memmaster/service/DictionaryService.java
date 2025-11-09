package at.abcdef.memmaster.service;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.repository.DictionaryRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DictionaryService {

  private DictionaryRepository dictionaryRepository;
  private UserService userService;

  public DictionaryService(DictionaryRepository dictionaryRepository, UserService userService) {
    this.dictionaryRepository = dictionaryRepository;
    this.userService = userService;
  }

  public List<Dictionary> getDictionaryInFolder(@Valid Folder folder) {
    return dictionaryRepository.getByFolders(Collections.singletonList(folder));
  }

  public List<Dictionary> saveDictionaryInFolder(Folder folder, List<Dictionary> dto) {
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
}
