package at.abcdef.memmaster.repository;

import at.abcdef.memmaster.model.Directory;
import at.abcdef.memmaster.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirectoryRepository extends JpaRepository<Directory, Long>, JpaSpecificationExecutor<Directory>
{
    Directory getByKeyAndType(String key, String type);

    List<Directory> getByType(String type);
}
