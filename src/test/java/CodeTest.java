import org.junit.Assert;
import org.junit.Test;
import parse.Code;

/**
 * @author qisy01
 * @create 19-4-22
 * @since 1.0.0
 */
public class CodeTest {

    @Test
    public void testDest() {
        Assert.assertEquals("111", Code.dest("AMD="));
    }

    @Test
    public void testJump() {
        Assert.assertEquals("010", Code.jump("aa;JEQ"));
    }

    @Test
    public void testCmp() throws Exception {
        Assert.assertEquals("0110010", Code.comp("A-1"));
    }
}
