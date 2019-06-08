package vmfinal;

import lombok.Getter;

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

        @Getter
        String code;

        SymbolKindEnum(String code) {
            this.code = code;
        }

        public static SymbolKindEnum getByCode(String code) {
            return SymbolKindEnum.valueOf(code);
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

        @Getter
        String code;

        MemoryKindEnum(String code) {
            this.code = code;
        }
    }

    public enum Arithmetic {
        /**
         * gt  >
         * lt <
         */
        ADD("add"),
        SUB("sub"),
        NEG("neg"),
        EQ("eq"),
        GT("gt"),
        LT("add"),
        AND("and"),
        OR("or"),
        NOT("not");

        @Getter
        String code;

        Arithmetic(String code) {
            this.code = code;
        }
    }

    /**
     * 数学运算符
     */
    public static final String MATH_PATTERN = "+-*/&|><=";
}
