package at.abcdef.memmaster.repository;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long>, JpaSpecificationExecutor<Dictionary> {
  List<Dictionary> getByFolders(List<Folder> folders);

  void deleteAllByFoldersContainingAndIdNotIn(Folder folder, List<Long> ids);

  Optional<Dictionary> findByNameAndFoldersContaining(String name, Folder folder);
}
