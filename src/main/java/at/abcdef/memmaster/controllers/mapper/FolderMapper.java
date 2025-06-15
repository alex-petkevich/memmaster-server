package at.abcdef.memmaster.controllers.mapper;

import at.abcdef.memmaster.controllers.dto.FolderDTO;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.util.BasicMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class})
public interface FolderMapper extends BasicMapper<Folder, FolderDTO>
{

    @Mapping(source = "user.id", target = "user_id")
    @Mapping(source = "isPublic", target = "is_public")
    @Mapping(source = "parentId", target = "parent_id")
    FolderDTO toEntity(Folder folder);

    @Mapping(source = "is_public", target = "isPublic")
    @Mapping(source = "parent_id", target = "parentId")
    Folder toDto(FolderDTO folderDTO);
}
