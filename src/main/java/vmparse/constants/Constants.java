package vmparse.constants;

import java.util.regex.Pattern;

/**
 * @Author qishiyu
 * @create 2019/5/5 19:49
 */
public interface Constants {
    /**
     * 关键字正则
     */
    String KEYWORD_PATTERN = "(^class$|^constructor$|^function$|^method$|^field$|" +
            "^static$|^var$|^int$|^char$|^boolean$|^void$|^true$|^false$|" +
            "^null$|^this$|^let$|^do$|^if$|^else$|^let$|^do$|^if$|^else$|^while$|" +
            "^return$)";

    /**
     * 符号正则
     */
    String SYMBOL_SPLIT_PATTERN = "((?<=(\\{|\\}|\\(|\\)|\\[|\\]|\\.|,|;|\\+|\\-|\\*|\\/|&|\\||<|>|=|~))|(?=(\\{|\\}|\\(|\\)|\\[|\\]|\\.|,|;|\\+|\\-|\\*|\\/|&|\\||<|>|=|~)))";

    String SYMBOL_PATTERN = "\\{|\\}|\\(|\\)|\\[|\\]|\\.|,|;|\\+|\\-|\\*|\\/|&|\\||<|>|=|~";
    /**
     * 32767
     */
    String INTEGER_PATTERN = "^([0-3][0-2][0-7][0-6][0-7]|[0-9]{0,3}[0-9])$";
    /**
     * identify pattern
     */
    String IDENTIFY_PATTERN = "^[^\\d]([a-z]|[A-Z]|[0-1]|_)*$";
    /**
     * string pattern
     */
    String STRING_PATTERN = "\".*\"";
    /**
     * 字符串匹配
     */
    String STRING_SPLIT = "((?<=\"[\\s\\S]{0,10000}\")|(?=\"[\\s\\S]{0,10000}\"))";

    Pattern STR_P = Pattern.compile(Constants.STRING_PATTERN);
    /**
     * blank
     */
    String BLANK_PATTERN = "\\t+|\\r+|\\n+";
    /**
     * 数学运算符
     */
    String MATH_PATTERN = "+-*/&|><=";

}
