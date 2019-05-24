import org.junit.Test;
import vmparse.constants.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author qishiyu
 * @create 2019/5/11 14:28
 */
public class BaseTest {
    static final Pattern STR_P = Pattern.compile("\".*\"");

    @Test
    public void baseTest() {
        Matcher matcher = STR_P.matcher(" \"string constant\"");
        if (matcher.find()) {
            System.out.println("s is ");
        }

    }

    @Test
    public void testFind() {
        System.out.println("+-*/&|><".contains(">"));
    }
}
