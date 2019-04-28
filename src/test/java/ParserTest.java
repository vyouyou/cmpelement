import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import parse.CommandTypeEnum;
import parse.Parser;

/**
 * @author qisy01
 * @create 19-4-24
 * @since 1.0.0
 */
public class ParserTest {

    private Parser parser;

    @Before
    public void construct() {
        parser = new Parser("");
    }

    @Test
    public void commandTypeTest() {
        Assert.assertEquals(parser.commandType("@i"), CommandTypeEnum.A_COMMAND);
        Assert.assertEquals(parser.commandType("(11)"), CommandTypeEnum.L_COMMAND);
        Assert.assertEquals(parser.commandType("1111"), CommandTypeEnum.C_COMMNAD);
    }

    @Test
    public void symbolTest(){
        Assert.assertEquals(parser.symbol("@i",CommandTypeEnum.A_COMMAND),"i");
    }

    @Test
    public void parserTest(){
        parser = new Parser("D:\\youyou\\nand2tetris\\projects\\06\\add\\Add.asm");
        parser.parse();
    }
}
