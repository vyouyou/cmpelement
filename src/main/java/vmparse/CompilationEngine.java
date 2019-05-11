package vmparse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.List;

/**
 * @Author qishiyu
 * @create 2019/5/5 19:27
 */
public class CompilationEngine {
    private int nodeIndex = 0;
    List<Element> nodeList;

    public CompilationEngine(String path) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File("rss.xml"));
            Element root = document.getRootElement();
            nodeList = root.elements();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void startParse() {

    }

    /**
     * 编译整个类
     */
    private void compileClass() {
    }

    /**
     * 编译静态变量或者字段
     */
    private void compileClassVarDec() {
    }

    /**
     * 编译方法
     */
    private void compileSubroutine() {
    }

    /**
     * 编译参数列表
     */
    private void compileParameterList() {
    }

    /**
     * do
     */
    private void compileDo() {
    }

    /**
     * let
     */
    private void compileLet() {
    }

    /**
     * while
     */
    private void compileWhile() {
    }

    /**
     * return
     */
    private void compileReturn() {
    }

    /**
     * if
     */
    private void compileIf() {
    }

    /**
     * 表达式 let i = (1 + 2 ) 后面为 expression
     */
    private void compileExpression() {
    }

    /**
     * 用于expression中的 参数 ( 1 + 2) 则 term  1
     */
    private void compileTerm() {
    }

    /**
     * 表达式列表 ，用于方法调用中 fun(a,b)
     */
    private void compileExpressionList() {
    }
}
