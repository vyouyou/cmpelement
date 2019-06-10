package vmparserfinal;

import org.junit.Test;
import vmfinal.CompilationEngine;
import vmfinal.JackTokenizer;
import vmfinal.TokenWithLineNumber;

import java.util.List;

/**
 * @Author qishiyu
 * @create 2019/5/8 20:03
 */
public class JackTokenizerTest {
    @Test
    public void genTokenTest() {
        JackTokenizer jackTokenizer = new JackTokenizer("D:\\javaprj\\cmpelement\\src\\test\\resource\\Main.jack");
    }

    @Test
    public void sevenTest() {
        JackTokenizer jackTokenizer = new JackTokenizer("D:\\javaprj\\cmpelement\\src\\test\\resource\\Seven\\Main.jack");
        List<TokenWithLineNumber> tokens = jackTokenizer.getTokenWithLineNumberList();
        new CompilationEngine(tokens, "Seven.vm").startParse();
    }

    @Test
    public void convertToBinTest() {
        JackTokenizer jackTokenizer = new JackTokenizer("D:\\javaprj\\cmpelement\\src\\test\\resource\\ConvertToBin\\Main.jack");
        List<TokenWithLineNumber> tokens = jackTokenizer.getTokenWithLineNumberList();
        new CompilationEngine(tokens, "ConvertToBin.vm").startParse();
    }
}
