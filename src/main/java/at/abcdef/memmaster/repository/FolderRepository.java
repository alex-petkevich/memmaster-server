package at.abcdef.memmaster.repository;

import at.abcdef.memmaster.model.Settings;
import at.abcdef.memmaster.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import at.abcdef.memmaster.model.Folder;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long>
{
    List<Folder> getByUserId(Integer userId);

    Settings getByUserIdAndName(Integer userId, String name);
}
