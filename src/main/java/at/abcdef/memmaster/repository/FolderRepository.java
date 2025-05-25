package at.abcdef.memmaster.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

import at.abcdef.memmaster.model.Folder;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long>, JpaSpecificationExecutor<Folder>
{
    List<Folder> getByUserId(Integer userId);

    Folder getByUserIdAndName(Integer userId, String name);
}
