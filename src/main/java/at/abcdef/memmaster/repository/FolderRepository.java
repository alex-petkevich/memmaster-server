package at.abcdef.memmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import at.abcdef.memmaster.model.Folder;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long>, JpaSpecificationExecutor<Folder>
{
    List<Folder> getByUserId(Integer userId);

    Folder getByUserIdAndName(Integer userId, String name);

    @Query(value = "select count(*) from dictionary_folders df where df.folder_id = :folderId", nativeQuery = true)
    long countByFolderIdInJoin(@Param("folderId") Long folderId);
}
