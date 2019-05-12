package vmparse;

import com.sun.org.apache.xpath.internal.compiler.Keywords;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import utils.FileUtils;
import vmparse.constants.KeyWordEnum;
import vmparse.constants.TokenTypeEnum;

import java.io.File;
import java.util.List;

/**
 * @Author qishiyu
 * @create 2019/5/5 19:27
 */
public class CompilationEngine {
    private int nodeIndex = 0;
    List<Element> nodeList;
    private Document targetDocument;

    public CompilationEngine(String path) {
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(new File(path));
            Element root = document.getRootElement();
            nodeList = root.elements();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    public void startParse(String targetName) {
        targetDocument = DocumentHelper.createDocument();
        while (nodeIndex < nodeList.size()) {
            Element ele = nodeList.get(nodeIndex);
            resolveToken(ele.getName(), ele.getText());
        }
        FileUtils.writeIntoXml(targetName, targetDocument);
    }

    /**
     * 处理token
     *
     * @param name
     * @param text
     */
    private void resolveToken(String name, String text) {
        if (name.equals(TokenTypeEnum.KEYWORD.getCode())) {
            if (text.equals(KeyWordEnum.CLASS.getCode())) {
                compileClass();
            }
            if (text.equals(KeyWordEnum.FUNCTION.getCode())) {
                compileSubroutine();
            }
        }

    }

    /**
     * 编译整个类
     */
    private void compileClass() {
        Element classEle = createKeywordElement(KeyWordEnum.CLASS);
        targetDocument.setRootElement(classEle);
        copyElement(classEle, 3);
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
        Element subRoutineEle = createKeywordElement(KeyWordEnum.FUNCTION);
        getClassElement().add(subRoutineEle);
        copyElement(subRoutineEle, 3);
        //处理参数列表
        compileParameterList(subRoutineEle);
        copyElement(subRoutineEle, 1);

    }


    /**
     * 编译参数列表
     */
    private void compileParameterList(Element subroutineEle) {
        final String PARAMETER_LIST = "parameterList";
        Element parameterListEle = DocumentHelper.createElement(PARAMETER_LIST);
        subroutineEle.add(parameterListEle);
        while (!getToken(nodeIndex).text.equals(")")) {
            copyElement(parameterListEle, 1);
        }
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

    /**
     * 获取class element
     * @return
     */
    private Element getClassElement(){
        return targetDocument.getRootElement();
    }

    /**
     * 根据enum创建element
     *
     * @param keyWordEnum
     * @return
     */
    private Element createKeywordElement(KeyWordEnum keyWordEnum) {
        return DocumentHelper.createElement(keyWordEnum.getTagName());
    }

    private void copyElement(Element parentEle, int num) {
        for (int i = 0; i < num; i++) {
            parentEle.add(getElementByIndex(nodeIndex++));
        }
    }

    private Token getToken(int index) {
        Element ele = nodeList.get(index);
        return new Token(ele.getName(), ele.getText());
    }

    private Element getElementByIndex(int index) {
        Element ele = nodeList.get(index);
        Element newEle = DocumentHelper.createElement(ele.getName());
        newEle.setText(ele.getText());
        return newEle;
    }

    @Data
    @AllArgsConstructor
    private class Token {
        private String name;

        private String text;
    }
}
