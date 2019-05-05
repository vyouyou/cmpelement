package vmparse;

import vmparse.constants.Constants;
import vmparse.constants.KeyWordEnum;
import vmparse.constants.TokenTypeEnum;

/**
 * @Author qishiyu
 * @create 2019/5/5 18:44
 */
public class JackTokenizer {
    private TokenTypeEnum getTokenType(String str){
        if (str.matches(Constants.SYMBOL_PATTERN))return TokenTypeEnum.SYMBOL;
        if (str.matches(Constants.INTEGER_PATTERN))return TokenTypeEnum.IDENTIFIER;
        if (str.matches(Constants.KEYWORD_PATTERN)) return TokenTypeEnum.KEYWORD;
        if (str.matches(Constants.STRING_PATTERN)) return TokenTypeEnum.STRING_CONST;
    }

    private KeyWordEnum getKeyWord(TokenTypeEnum tokenTypeEnum,String str){

    }
}
