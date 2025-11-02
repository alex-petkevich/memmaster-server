package at.abcdef.memmaster.controllers.mapper;

import at.abcdef.memmaster.repository.DirectoryRepository;
import org.mapstruct.Mapper;

import at.abcdef.memmaster.controllers.dto.UserDTO;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.util.BasicMapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BasicMapper<User, UserDTO>
{
}
