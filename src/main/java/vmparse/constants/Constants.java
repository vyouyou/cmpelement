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
}
