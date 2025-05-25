package at.abcdef.memmaster.controllers.mapper;

import at.abcdef.memmaster.controllers.dto.response.FolderDTO;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.util.BasicMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class})
public interface FolderMapper extends BasicMapper<Folder, FolderDTO>
{

    @Mapping(source = "user.id", target = "user_id")
    @Mapping(source = "isPublic", target = "is_public")
    FolderDTO toEntity(Folder folder);

    @Mapping(source = "is_public", target = "isPublic")
    Folder toDto(FolderDTO folderDTO);
}
