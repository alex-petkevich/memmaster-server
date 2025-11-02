package at.abcdef.memmaster.controllers.mapper;

import at.abcdef.memmaster.controllers.dto.RoleDTO;
import at.abcdef.memmaster.model.Role;
import at.abcdef.memmaster.util.BasicMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = { RoleMapper.class})
public interface RoleMapper extends BasicMapper<Role, RoleDTO>
{
    RoleDTO toEntity(Role role);
}
