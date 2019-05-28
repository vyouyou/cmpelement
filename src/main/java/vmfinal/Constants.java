package vmfinal;

/**
 * @Author qishiyu
 * @create 2019/5/27 21:11
 */
public class Constants {
    public enum SymbolKindEnum {
        STATIC("static"),
        FIELD("field"),
        ARG("arg"),
        VAR("var");

        String code;

        SymbolKindEnum(String code) {
            this.code = code;
        }
    }

    public enum MemoryKindEnum {
        CONST("const"),
        ARG("arg"),
        LOCAL("local"),
        STATIC("static"),
        THIS("this"),
        THAT("that"),
        POINTER("pointer"),
        TEMP("temp");

        String code;

        MemoryKindEnum(String code) {
            this.code = code;
        }
    }

    public enum Arithmetic {
        ADD("add"),
        SUB("sub"),
        NEG("neg"),
        EQ("eq"),
        GT("add"),
        LT("add"),
        AND("add"),
        OR("add"),
        NOT("add");

        String code;

        Arithmetic(String code) {
            this.code = code;
        }
    }
}
