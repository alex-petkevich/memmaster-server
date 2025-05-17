package at.abcdef.memmaster.controllers.mapper;

import at.abcdef.memmaster.controllers.dto.response.SettingsResponse;
import at.abcdef.memmaster.model.Settings;
import at.abcdef.memmaster.util.BasicMapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = { UserMapper.class})
public interface SettingsMapper extends BasicMapper<Settings, SettingsResponse>
{

    @Mapping(source = "user.id", target = "user_id")
    SettingsResponse toEntity(Settings settings);


}
