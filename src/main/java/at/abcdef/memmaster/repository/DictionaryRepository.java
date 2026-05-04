package at.abcdef.memmaster.repository;

import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DictionaryRepository extends JpaRepository<Dictionary, Long>, JpaSpecificationExecutor<Dictionary> {
  @Query("SELECT d FROM Dictionary d JOIN d.folders f WHERE f IN :folders")
  List<Dictionary> getByFolders(@Param("folders") List<Folder> folders);

  void deleteAllByFoldersContainingAndIdNotIn(Folder folder, List<Long> ids);

  Optional<Dictionary> findByNameAndFoldersContaining(String name, Folder folder);

  boolean existsByIdAndFoldersContaining(Long id, Folder folder);
}
