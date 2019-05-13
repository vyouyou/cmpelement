package vmparse;

import com.sun.org.apache.xpath.internal.compiler.Keywords;
import jdk.nashorn.internal.parser.TokenType;
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
        // 处理 )
        copyElement(subRoutineEle, 1);
        Element subRoutineBodyEle = DocumentHelper.createElement("subroutineBody");
        subRoutineEle.add(subRoutineBodyEle);
        // 处理 {
        copyElement(subRoutineBodyEle, 1);
        while (getToken(nodeIndex).getText().equals(KeyWordEnum.VAR.getCode())) {
            compileVar(subRoutineBodyEle);
        }
        // statementLists
        compileStatementList(subRoutineBodyEle);

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
    private void compileDo(Element parentEle) {
    }

    /**
     * let
     */
    private void compileLet(Element parentEle) {
        Element letEle = createKeywordElement(KeyWordEnum.LET);
        parentEle.add(letEle);
        do {
            copyElement(letEle);
        } while (!getToken(nodeIndex).getText().equals("="));
        compileExpression(letEle);
    }

    /**
     * var
     */
    private void compileVar(Element parentEle) {
        Element varEle = createKeywordElement(KeyWordEnum.VAR);
        parentEle.add(varEle);
        do {
            copyElement(varEle);
        } while (!getToken(nodeIndex).getText().equals(";"));
    }

    /**
     * while
     */
    private void compileWhile(Element parentEle) {
    }

    /**
     * return
     */
    private void compileReturn(Element parentEle) {
    }

    /**
     * if
     */
    private void compileIf(Element parentEle) {
    }

    /**
     * 表达式列表 ，用于方法调用中 fun(a,b)
     */
    private void compileStatementList(Element parentEle) {
        Element statementListEle = DocumentHelper.createElement("statements");
        parentEle.add(statementListEle);
        // 当前节点
        for (; ; ) {
            Token token = getToken(nodeIndex);
            if (token.getName().equals(TokenTypeEnum.KEYWORD.getCode())) {
                String text = token.getText();
                if (text.equals(KeyWordEnum.LET.getCode())) {
                    compileLet(statementListEle);
                } else if (text.equals(KeyWordEnum.VAR.getCode())) {
                    compileVar(statementListEle);
                } else if (text.equals(KeyWordEnum.DO.getCode())) {
                    compileDo(statementListEle);
                } else if (text.equals(KeyWordEnum.IF.getCode())) {
                    compileIf(statementListEle);
                } else if (text.equals(KeyWordEnum.WHILE.getCode())) {
                    compileWhile(statementListEle);
                } else if (text.equals(KeyWordEnum.RETURN.getCode())) {
                    compileReturn(statementListEle);
                } else {
                    System.out.println("遇到无法识别的关键字");
                    break;
                }
            }
        }
    }

    private void compileExpressionList(Element parentEle) {

    }

    /**
     * 表达式 let i = (1 + 2 ) 后面为 expression
     */
    private void compileExpression(Element parentEle) {
        Element expressionEle = DocumentHelper.createElement("expression");
        parentEle.add(expressionEle);
        String tokenName = getToken(nodeIndex).getName();
        if (tokenName.equals(TokenTypeEnum.IDENTIFIER.getCode()) ||
                tokenName.equals(TokenTypeEnum.INT_CONST.getCode()) ||
                tokenName.equals(TokenTypeEnum.STRING_CONST.getCode())
        ) {
            compileTerm(expressionEle);
        }
    }

    /**
     * 用于expression中的 参数 ( 1 + 2) 则 term  1
     */
    private void compileTerm(Element parentEle) {
        Element termEle = DocumentHelper.createElement("term");
        copyElement(termEle);
        String text = getToken().getText();
        if (text.equals("(")||text.equals(".")) {

        }
    }


    /**
     * 获取class element
     *
     * @return
     */
    private Element getClassElement() {
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

    private void copyElement(Element parentEle) {
        parentEle.add(getElementByIndex(nodeIndex++));
    }

    private void copyElement(Element parentEle, int num) {
        for (int i = 0; i < num; i++) {
            copyElement(parentEle);
        }
    }

    private Token getToken() {
        return getToken(nodeIndex);
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
