package at.abcdef.memmaster.model.yandex;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YTr {

    private String text;
    private String pos;
    private String gen;
    private Integer fr;
    private YMean[] mean;
    private YSyn[] syn;
}
