package vmparse.constants;

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
    String SYMBOL_PATTERN = "({|}|\\(|\\)|\\[|\\]|\\.|,|;|\\+|\\-|\\*|\\/|&|\\||<|>|=|~)";
    /**
     * 32767
     */
    String INTEGER_PATTERN = "^([0-3][0-2][0-7][0-6][0-7]|[0-9]{0,3}[0-9])$";
    /**
     * identify pattern
     */
    String IDENTIFY_PATTERN = "^[^\\d]([a-z]|[0-1]|_)*$";
}
