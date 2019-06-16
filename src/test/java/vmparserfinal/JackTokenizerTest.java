package vmparserfinal;

import com.google.common.io.Resources;
import org.junit.Test;
import vmfinal.CompilationEngine;
import vmfinal.JackTokenizer;
import vmfinal.TokenWithLineNumber;

import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * @Author qishiyu
 * @create 2019/5/8 20:03
 */
public class JackTokenizerTest {
    @Test
    public void genTokenTest() {
        JackTokenizer jackTokenizer = new JackTokenizer("E:\\javaprj\\cmpelement\\src\\test\\resource\\Main.jack");
    }

    @Test
    public void sevenTest() {
        JackTokenizer jackTokenizer = new JackTokenizer("E:\\javaprj\\cmpelement\\src\\test\\resource\\Seven\\Main.jack");
        List<TokenWithLineNumber> tokens = jackTokenizer.getTokenWithLineNumberList();
        new CompilationEngine(tokens, "Seven.vm").startParse();
    }

    @Test
    public void convertToBinTest() {
        JackTokenizer jackTokenizer = new JackTokenizer("E:\\javaprj\\cmpelement\\src\\test\\resource\\ConvertToBin\\Main.jack");
        List<TokenWithLineNumber> tokens = jackTokenizer.getTokenWithLineNumberList();
        new CompilationEngine(tokens, "ConvertToBin.vm").startParse();
    }

    @Test
    public void squareDance() {
        compileDirectory("E:\\javaprj\\cmpelement\\src\\test\\resource\\Square");
    }

    @Test
    public void average() {
        compileDirectory("E:\\javaprj\\cmpelement\\src\\test\\resource\\Average");
    }

    @Test
    public void pong() {
        compileDirectory("E:\\javaprj\\cmpelement\\src\\test\\resource\\Pong");
    }

    @Test
    public void complexArrays() {
        compileDirectory("E:\\javaprj\\cmpelement\\src\\test\\resource\\ComplexArrays");
    }

    private void compileDirectory(String path) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            for (File childFile : childFiles) {
                String fullPath = childFile.getAbsolutePath();
                if (fullPath.endsWith(".jack")) {
                    JackTokenizer jackTokenizer = new JackTokenizer(fullPath);
                    List<TokenWithLineNumber> tokens = jackTokenizer.getTokenWithLineNumberList();
                    String[] paths = fullPath.split("\\\\");
                    String vmName = paths[paths.length - 1].replace("jack", "vm");
                    new CompilationEngine(tokens, vmName).startParse();
                }
            }
        }
    }

}
