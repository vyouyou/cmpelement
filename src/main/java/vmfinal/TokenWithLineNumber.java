package vmfinal;

import lombok.Data;
import vmparse.constants.TokenTypeEnum;

/**
 * @Author qishiyu
 * @create 2019/6/1 10:25
 */
@Data
public class TokenWithLineNumber {

    public TokenWithLineNumber(String text, Integer lineNum) {
        this.text = text;
        this.lineNum = lineNum;
    }

    private TokenTypeEnum type;

    private String text;

    private Integer lineNum;
}