package at.abcdef.memmaster.model.yandex;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class YDef {
    private String text;
    private String pos;
    private List<YTr> tr;
}
