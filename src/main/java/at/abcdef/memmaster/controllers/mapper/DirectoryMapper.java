package at.abcdef.memmaster.controllers.mapper;

import at.abcdef.memmaster.controllers.dto.DirectoryDTO;
import at.abcdef.memmaster.model.Directory;
import at.abcdef.memmaster.util.BasicMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DirectoryMapper extends BasicMapper<Directory, DirectoryDTO>
{
}
