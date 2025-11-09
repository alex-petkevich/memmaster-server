package at.abcdef.memmaster.controllers.mapper;

import at.abcdef.memmaster.controllers.dto.DictionaryDTO;
import at.abcdef.memmaster.controllers.dto.DictionaryPairDTO;
import at.abcdef.memmaster.controllers.dto.FolderDTO;
import at.abcdef.memmaster.model.Dictionary;
import at.abcdef.memmaster.model.Folder;
import at.abcdef.memmaster.util.BasicMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class})
public interface DictionaryMapper extends BasicMapper<Dictionary, DictionaryDTO>
{

    @Mapping(source = "user.id", target = "user_id")
    DictionaryDTO toEntity(Dictionary dictionary);

    Dictionary toDto(DictionaryDTO dictionaryDTO);

    default DictionaryPairDTO toPairDTO(Dictionary dictionary)
    {
        if (dictionary == null) {
            return null;
        }

        DictionaryPairDTO dictionaryPairDTO = new DictionaryPairDTO();
        dictionaryPairDTO.setId(dictionary.getId());
        dictionaryPairDTO.setName(dictionary.getName());
        dictionaryPairDTO.setValue(dictionary.getValue());
        dictionaryPairDTO.setName_file(dictionary.getNameImg());
        dictionaryPairDTO.setValue_file(dictionary.getValueImg());
        dictionaryPairDTO.setName_type(StringUtils.hasText(dictionary.getName()) ? DictionaryPairDTO.PairType.TEXT : DictionaryPairDTO.PairType.FILE );
        dictionaryPairDTO.setValue_type(StringUtils.hasText(dictionary.getValue()) ? DictionaryPairDTO.PairType.TEXT : DictionaryPairDTO.PairType.FILE );
        return dictionaryPairDTO;
    }

    default List<DictionaryPairDTO> toPairDTO(List<Dictionary> dictionaryList)
    {
        if (dictionaryList == null) {
            return null;
        }

        List<DictionaryPairDTO> list = new ArrayList<>(dictionaryList.size());
        for (Dictionary dictionary : dictionaryList) {
            list.add(toPairDTO(dictionary));
        }
        return list;
    }

    default Dictionary fromPairDto(DictionaryPairDTO dictionaryPairDTO)
    {
        if (dictionaryPairDTO == null) {
            return null;
        }

        Dictionary dictionary = new Dictionary();
        dictionary.setId(dictionaryPairDTO.getId());
        dictionary.setName(dictionaryPairDTO.getName());
        dictionary.setValue(dictionaryPairDTO.getValue());
        dictionary.setNameImg(dictionaryPairDTO.getName_file());
        dictionary.setValueImg(dictionaryPairDTO.getValue_file());
        return dictionary;
    }

    default List<Dictionary> fromPairDto(List<DictionaryPairDTO> dictionaryPairDTOList)
    {
        if (dictionaryPairDTOList == null) {
            return null;
        }

        List<Dictionary> list = new ArrayList<>(dictionaryPairDTOList.size());
        for (DictionaryPairDTO dictionaryPairDTO : dictionaryPairDTOList) {
            list.add(fromPairDto(dictionaryPairDTO));
        }
        return list;
    }
}
