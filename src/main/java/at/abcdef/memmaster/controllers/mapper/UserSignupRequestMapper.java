package at.abcdef.memmaster.controllers.mapper;

import at.abcdef.memmaster.controllers.dto.UserDTO;
import at.abcdef.memmaster.model.User;
import at.abcdef.memmaster.util.BasicMapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserSignupRequestMapper extends BasicMapper<User, UserDTO>
{
    default Boolean map(Integer value) {
        return value > 0 ? Boolean.TRUE : Boolean.FALSE;
    }

    default Integer map(Boolean value) {
        return Boolean.TRUE.equals(value) ? 1 : 0;
    }
}
