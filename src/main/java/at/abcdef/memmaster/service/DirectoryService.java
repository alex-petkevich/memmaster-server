package at.abcdef.memmaster.service;

import at.abcdef.memmaster.model.Directory;
import at.abcdef.memmaster.repository.DirectoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DirectoryService {
   private final DirectoryRepository directoryRepository;
   public DirectoryService(DirectoryRepository directoryRepository) {
      this.directoryRepository = directoryRepository;
   }

   public Directory getDirectoryByKeyAndType(String key, String type) {
      return directoryRepository.getByKeyAndType(key, type);
   }

   public List<Directory> getDirectoriesByType(String type) {
      return directoryRepository.getByType(type);
   }
}
