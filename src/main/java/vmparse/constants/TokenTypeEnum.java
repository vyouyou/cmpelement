package vmparse.constants;


import lombok.Getter;

/**
 * @Author qishiyu
 * @create 2019/5/5 18:47
 */
public enum TokenTypeEnum {
    /**
     * token类型枚举
     */
    KEYWORD("keyword"),
    SYMBOL("symbol"),
    IDENTIFIER("identifier"),
    INT_CONST("integerConstant"),
    STRING_CONST("stringConstant");

    @Getter
    private String code;

    TokenTypeEnum(String code){
        this.code = code;
    }
}
