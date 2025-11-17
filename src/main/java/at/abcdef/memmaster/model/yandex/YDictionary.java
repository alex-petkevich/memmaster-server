package at.abcdef.memmaster.model.yandex;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class YDictionary {
    private Object head;
    private List<YDef> def;
    private Integer nmt_code;
    private Integer code;
}
