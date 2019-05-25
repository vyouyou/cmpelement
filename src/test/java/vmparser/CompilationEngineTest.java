package vmparser;

import org.junit.Test;
import vmparse.CompilationEngine;

/**
 * @Author qishiyu
 * @create 2019/5/11 16:37
 */
public class CompilationEngineTest {
    @Test
    public void compileClassTest(){
        CompilationEngine engine = new CompilationEngine("SquareGameT.xml");
        engine.startParse("SquareGame.xml");
    }

    @Test
    public void compileSquareClassTest(){
        CompilationEngine engine = new CompilationEngine("SquareT.xml");
        engine.startParse("Square.xml");
    }
}
