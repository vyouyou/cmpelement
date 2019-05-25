package vmparse;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import utils.FileUtils;
import vmparse.constants.Constants;
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
    private String targetName;

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
        this.targetName = targetName;
        targetDocument = DocumentHelper.createDocument();
        compileClass();
        FileUtils.writeIntoXml(targetName, targetDocument);
    }

    /**
     * 编译整个类
     */
    private void compileClass() {
        Element classEle = createKeywordElement(KeyWordEnum.CLASS);
        targetDocument.setRootElement(classEle);
        copyElement(classEle, 3);
        while (!"}".equals(getToken().getText())) {
            String text = getToken().getText();
            if (text.equals(KeyWordEnum.STATIC.getCode()) ||
                    text.equals(KeyWordEnum.FIELD.getCode())) {
                compileClassVarDec(classEle);
            } else if (text.equals(KeyWordEnum.METHOD.getCode()) ||
                    text.equals(KeyWordEnum.FUNCTION.getCode()) ||
                    text.equals(KeyWordEnum.CONSTRUCTOR.getCode())) {
                compileSubroutine(classEle);
            }
        }
        copyElement(classEle);
    }

    /**
     * 编译静态变量或者字段
     */
    private void compileClassVarDec(Element parentEle) {
        Element classVarEle = DocumentHelper.createElement("classVarDec");
        parentEle.add(classVarEle);
        // static field
        copyElement(classVarEle, 3);
        do {
            if (";".equals(getToken().getText())) {
                copyElement(classVarEle);
                break;
            } else if (",".equals(getToken().getText())) {
                copyElement(classVarEle, 2);
            }
        } while (true);
    }

    /**
     * 编译方法
     */
    private void compileSubroutine(Element parentEle) {
        Element subRoutineEle = createKeywordElement(KeyWordEnum.FUNCTION);
        parentEle.add(subRoutineEle);
        copyElement(subRoutineEle, 4);
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
        // 处理 }
        copyElement(subRoutineBodyEle);
    }


    /**
     * 编译参数列表
     */
    private void compileParameterList(Element subroutineEle) {
        final String PARAMETER_LIST = "parameterList";
        Element parameterListEle = DocumentHelper.createElement(PARAMETER_LIST);
        subroutineEle.add(parameterListEle);
        while (!getToken(nodeIndex).getText().equals(")")) {
            copyElement(parameterListEle, 1);
        }
    }

    /**
     * do
     * do aaa(exp1,exp2)
     */
    private void compileDo(Element parentEle) {
        Element doEle = createKeywordElement(KeyWordEnum.DO);
        parentEle.add(doEle);
        do {
            copyElement(doEle);
        } while (!getToken(nodeIndex - 1).getText().equals("("));
        compileExpressionList(doEle);
        copyElement(doEle, 2);
    }

    /**
     * let
     */
    private void compileLet(Element parentEle) {
        Element letEle = createKeywordElement(KeyWordEnum.LET);
        parentEle.add(letEle);
        do {
            String text = getToken().getText();
            copyElement(letEle);
            // 处理刑辱 let a[1]
            if (text.equals("[")) {
                compileExpression(letEle);
            }
        } while (!getToken(nodeIndex - 1).getText().equals("="));
        compileExpression(letEle);
        // 处理；
        copyElement(letEle);
    }

    /**
     * var
     */
    private void compileVar(Element parentEle) {
        Element varEle = createKeywordElement(KeyWordEnum.VAR);
        parentEle.add(varEle);
        do {
            copyElement(varEle);
        } while (!getToken(nodeIndex - 1).getText().equals(";"));
    }

    /**
     * while
     */
    private void compileWhile(Element parentEle) {
        Element whileEle = createKeywordElement(KeyWordEnum.WHILE);
        parentEle.add(whileEle);
        // while (
        copyElement(whileEle, 2);
        compileExpression(whileEle);
        // )  }
        copyElement(whileEle, 2);
        compileStatementList(whileEle);
        // }
        copyElement(whileEle);
    }

    /**
     * return
     */
    private void compileReturn(Element parentEle) {
        Element returnEle = createKeywordElement(KeyWordEnum.RETURN);
        parentEle.add(returnEle);
        copyElement(returnEle);
        if (!";".equals(getToken().getText())) {
            compileExpression(returnEle);
        }
        // copy ;
        copyElement(returnEle);
    }

    /**
     * if
     */
    private void compileIf(Element parentEle) {
        Element ifEle = createKeywordElement(KeyWordEnum.IF);
        parentEle.add(ifEle);
        // if  (
        copyElement(ifEle, 2);
        compileExpression(ifEle);
        // )  {
        copyElement(ifEle, 2);
        compileStatementList(ifEle);
        // }
        copyElement(ifEle);
        //compile else
        if (KeyWordEnum.ELSE.getCode().equals(getToken().getText())) {
            // else {
            copyElement(ifEle, 2);
            compileStatementList(ifEle);
            // }
            copyElement(ifEle);
        }
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
            } else if ("}".equals(token.getText())) {
                System.out.println("statement parse end");
                break;
            } else {
                FileUtils.writeIntoXml("Main.xml", targetDocument);
                throw new RuntimeException("遇到不为keyword的节点" + nodeIndex + getToken().getText() + getToken().getName());
            }
        }
    }

    private void compileExpressionList(Element parentEle) {
        Element expListEle = DocumentHelper.createElement("expressionList");
        parentEle.add(expListEle);
        while (!")".equals(getToken().getText())) {
            compileExpression(expListEle);
            if (")".equals(getToken().getText())) {
                break;
            }
            copyElement(expListEle);
        }
    }

    /**
     * 表达式 let i = (1 + 2 ) 后面为 expression
     */
    private void compileExpression(Element parentEle) {
        Element expressionEle = DocumentHelper.createElement("expression");
        parentEle.add(expressionEle);
        compileTerm(expressionEle);
        while (Constants.MATH_PATTERN.contains(getToken().getText())) {
            System.out.println(getToken().getText());
            copyElement(expressionEle);
            compileTerm(expressionEle);
        }
    }

    /**
     * 用于expression中的 参数 ( 1 + 2) 则 term  1
     * a.b(<explist>c,d,e</explist>)
     * [ <term>i</term> ]
     * <term>a.b(c)</term>
     */
    private void compileTerm(Element parentEle) {
        Element termEle = DocumentHelper.createElement("term");
        parentEle.add(termEle);
        // 根据这个token来判断下来做的事情
        Token curToken = getToken();
        copyElement(termEle);
        String text = curToken.getText();
        String type = curToken.getName();
        if (type.equals(TokenTypeEnum.IDENTIFIER.getCode())) {
            // 如  a() a.b() a[i]
            Token nextToken = getToken();
            if ("(".equals(nextToken.getText()) || ".".equals(nextToken.getText())) {
                this.compilePartCall(termEle);
//                copyElement(termEle);
            } else if ("[".equals(nextToken.getText())) {
                // 复制 [
                copyElement(termEle);
                compileExpression(termEle);
                if ("]".equals(getToken().getText())) {
                    copyElement(termEle);
                } else {
                    System.out.println("遇到了非法的字符");
                }
//                copyElement(termEle);
            } else {
//                copyElement(termEle);
                //                nodeIndex--;

            }
        } else if (type.equals(TokenTypeEnum.STRING_CONST.getCode()) ||
                type.equals(TokenTypeEnum.INT_CONST.getCode())) {
//            copyElement(termEle);
        } else {
            if ("-".equals(text) || "~".equals(text)) {
                compileTerm(termEle);
            } else if ("null".equals(text)
                    || "false".equals(text)
                    || "true".equals(text)
                    || "this".equals(text)
            ) {
                //TODO 这里
                //刚才已经copy过了
//                copyElement(termEle);
            } else if ("(".equals(text)) {
                compileExpression(termEle);
                if (")".equals(getToken().getText())) {
                    copyElement(termEle);
                } else {
                    System.out.println("can not get )");
                }
            } else {
                System.out.println("term get unExpect symbol");
            }
        }
    }

    private void compilePartCall(Element parentEle) {
        Token token = getToken();
        String text = token.getText();
        if ("(".equals(text)) {
            copyElement(parentEle);
            compileExpressionList(parentEle);
            if (")".equals(getToken().getText())) {
                copyElement(parentEle);
            } else {
                System.out.println("error is compilePartCall miss )");
            }
        } else if (".".equals(text)) {
            copyElement(parentEle);
            if (!TokenTypeEnum.IDENTIFIER.getCode().equals(getToken().getName())) {
                System.out.println("error is compilePartCall not IDENTIFIER");
            }
            copyElement(parentEle);
            if (!"(".equals(getToken().getText())) {
                System.out.println("error is compilePartCall not has (");
            }
            copyElement(parentEle);
            compileExpressionList(parentEle);
            if (!")".equals(getToken(nodeIndex).getText())) {
                System.out.println("error is compilePartCall not has )");
            }
            copyElement(parentEle);
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
        if (index == nodeList.size()) {
            FileUtils.writeIntoXml(targetName, targetDocument);
            return null;
        }
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

        public String getText() {
            return text.trim();
        }
    }
}
