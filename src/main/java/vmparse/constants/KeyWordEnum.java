package vmparse.constants;

import lombok.Getter;

/**
 * @Author qishiyu
 * @create 2019/5/5 18:48
 */
public enum KeyWordEnum {
    /**
     * 关键字
     */
    CLASS("class", "class"),
    METHOD("method", "subroutineDec"),
    INT("int", ""),
    FUNCTION("function", "subroutineDec"),
    BOOLEAN("boolean", ""),
    CONSTRUCTOR("constructor", ""),
    CHAR("char", ""),
    VOID("void", ""),
    VAR("var", "varDec"),
    STATIC("static", ""),
    FIELD("field", ""),
    LET("let", "let"),
    DO("do", "do"),
    IF("if", "if"),
    ELSE("else", ""),
    WHILE("while", "while"),
    RETURN("return", "return"),
    TRUE("true", ""),
    FALSE("false", ""),
    NULL("null", ""),
    THIS("this", "");

    @Getter
    String code;
    @Getter
    String tagName;

    KeyWordEnum(String code, String tagName) {
        this.code = code;
        this.tagName = tagName;
    }
}
