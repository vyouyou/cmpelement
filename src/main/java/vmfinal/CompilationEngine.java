package vmfinal;

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
import java.util.Stack;

/**
 * @Author qishiyu
 * @create 2019/5/5 19:27
 */
public class CompilationEngine {
    /**
     * 常量栈
     */
    private Stack<SymbolTable> symbolTableStack;

    private List<TokenWithLineNumber> tokens;
    private int tokenIndex = 0;
    private VMWriter vmWriter;
    /**
     * 类名
     */
    private String className;

    public CompilationEngine(List<TokenWithLineNumber> tokens) {
        symbolTableStack = new Stack<>();
        this.tokens = tokens;
        this.vmWriter = new VMWriter("Main.vm");
    }

    public void startParse(String targetName) {
        compileClass();
        compileClassVarDec();
        while (KeyWordEnum.CONSTRUCTOR.getCode().equals(getTokenText()) ||
                KeyWordEnum.FUNCTION.getCode().equals(getTokenText()) ||
                KeyWordEnum.METHOD.getCode().equals(getTokenText())
        ) {
            compileSubroutine();
        }
    }

    /**
     * 编译整个类
     */
    private void compileClass() {
        if ("class".equals(getTokenText())) {
            tokenIndex++;
            className = getTokenText();
            vmWriter.setClassName(className);
            tokenIndex++;
        } else {
            throw new CompileException("没有class关键字");
        }
//        Element classEle = createKeywordElement(KeyWordEnum.CLASS);
//        targetDocument.setRootElement(classEle);
//        copyElement(classEle, 3);
//        while (!"}".equals(getToken().getText())) {
//            String text = getToken().getText();
//            if (text.equals(KeyWordEnum.STATIC.getCode()) ||
//                    text.equals(KeyWordEnum.FIELD.getCode())) {
//                compileClassVarDec(classEle);
//            } else if (text.equals(KeyWordEnum.METHOD.getCode()) ||
//                    text.equals(KeyWordEnum.FUNCTION.getCode()) ||
//                    text.equals(KeyWordEnum.CONSTRUCTOR.getCode())) {
//                compileSubroutine(classEle);
//            }
//        }
//        copyElement(classEle);
    }

    /**
     * 编译静态变量或者字段
     */
    private void compileClassVarDec() {
        SymbolTable classSymbolTable = new SymbolTable();
        while (KeyWordEnum.STATIC.getCode().equals(getTokenText())
                || KeyWordEnum.FIELD.getCode().equals(getTokenText())) {
            String keyword = getTokenText();
            tokenIndex++;
            String type = getTokenText();
            tokenIndex++;
            while (true) {
                String identify = getTokenText();
                classSymbolTable.define(identify, type, vmfinal.Constants.SymbolKindEnum.getByCode(keyword));
                tokenIndex++;
                if (";".equals(getTokenText())) {
                    break;
                }
            }
        }
        symbolTableStack.push(classSymbolTable);
    }

    /**
     * 编译方法
     */
    private void compileSubroutine() {
        tokenIndex++;
        String name = getTokenText();
        while (!"{".equals(getTokenText())) {
            tokenIndex++;
        }
        int argsNum = compileVar();
        vmWriter.writeFunction(getTokenText(), argsNum);
        compileStatementList();
    }


    /**
     * 编译参数列表
     */
    private void compileParameterList() {

    }

    /**
     * do
     * do aaa(exp1,exp2)
     */
    private void compileDo() {
        tokenIndex += 2;
        // 先获取方法名称
        String funcName = getTokenText();
        compileParameterList();
    }

    /**
     * let
     */
    private void compileLet(Element parentEle) {
//        Element letEle = createKeywordElement(KeyWordEnum.LET);
//        parentEle.add(letEle);
//        do {
//            String text = getToken().getText();
//            copyElement(letEle);
//            // 处理刑辱 let a[1]
//            if (text.equals("[")) {
//                compileExpression(letEle);
//            }
//        } while (!getToken(nodeIndex - 1).getText().equals("="));
//        compileExpression(letEle);
//        // 处理；
//        copyElement(letEle);
    }

    /**
     * var
     */
    private int compileVar() {
        SymbolTable methodSymbol = new SymbolTable();
        int argsNum = 0;
        while (KeyWordEnum.VAR.getCode().equals(getTokenText())) {
            tokenIndex++;
            String type = getTokenText();
            while (true) {
                argsNum++;
                tokenIndex++;
                String identify = getTokenText();
                methodSymbol.define(identify, type, vmfinal.Constants.SymbolKindEnum.VAR);
                tokenIndex++;
                if (";".equals(getTokenText())) {
                    tokenIndex++;
                    break;
                }
            }
        }
        return argsNum;
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
        compileStatementList();
        // }
        copyElement(whileEle);
    }

    /**
     * return
     */
    private void compileReturn(Element parentEle) {
//        Element returnEle = createKeywordElement(KeyWordEnum.RETURN);
//        parentEle.add(returnEle);
//        copyElement(returnEle);
//        if (!";".equals(getToken().getText())) {
//            compileExpression(returnEle);
//        }
//        // copy ;
//        copyElement(returnEle);
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
        compileStatementList();
        // }
        copyElement(ifEle);
        //compile else
//        if (KeyWordEnum.ELSE.getCode().equals(getToken().getText())) {
//            // else {
//            copyElement(ifEle, 2);
//            compileStatementList();
//            // }
//            copyElement(ifEle);
//        }
    }


    /**
     * 表达式列表 ，用于方法调用中 fun(a,b)
     */
    private void compileStatementList() {
        // 当前节点
        while (true) {
            if (KeyWordEnum.DO.getCode().equals(getTokenText())) {
                compileDo();
            }
        }
//        for (; ; ) {
//            vmparse.CompilationEngine.Token token = getToken(nodeIndex);
//            if (token.getName().equals(TokenTypeEnum.KEYWORD.getCode())) {
//                String text = token.getText();
//                if (text.equals(KeyWordEnum.LET.getCode())) {
//                    compileLet(statementListEle);
//                } else if (text.equals(KeyWordEnum.VAR.getCode())) {
//                    compileVar(statementListEle);
//                } else if (text.equals(KeyWordEnum.DO.getCode())) {
//                    compileDo(statementListEle);
//                } else if (text.equals(KeyWordEnum.IF.getCode())) {
//                    compileIf(statementListEle);
//                } else if (text.equals(KeyWordEnum.WHILE.getCode())) {
//                    compileWhile(statementListEle);
//                } else if (text.equals(KeyWordEnum.RETURN.getCode())) {
//                    compileReturn(statementListEle);
//                } else {
//                    System.out.println("遇到无法识别的关键字");
//                    break;
//                }
//            } else if ("}".equals(token.getText())) {
//                System.out.println("statement parse end");
//                break;
//            } else {
//                FileUtils.writeIntoXml("Main.xml", targetDocument);
//                throw new RuntimeException("遇到不为keyword的节点" + nodeIndex + getToken().getText() + getToken().getName());
//            }
//        }
    }

    private void compileExpressionList(Element parentEle) {
//        Element expListEle = DocumentHelper.createElement("expressionList");
//        parentEle.add(expListEle);
//        while (!")".equals(getToken().getText())) {
//            compileExpression(expListEle);
//            if (")".equals(getToken().getText())) {
//                break;
//            }
//            copyElement(expListEle);
//        }
    }

    /**
     * 表达式 let i = (1 + 2 ) 后面为 expression
     */
    private void compileExpression(Element parentEle) {
//        Element expressionEle = DocumentHelper.createElement("expression");
//        parentEle.add(expressionEle);
//        compileTerm(expressionEle);
//        while (Constants.MATH_PATTERN.contains(getToken().getText())) {
//            System.out.println(getToken().getText());
//            copyElement(expressionEle);
//            compileTerm(expressionEle);
//        }
    }

    /**
     * 用于expression中的 参数 ( 1 + 2) 则 term  1
     * a.b(<explist>c,d,e</explist>)
     * [ <term>i</term> ]
     * <term>a.b(c)</term>
     */
    private void compileTerm(Element parentEle) {
//        Element termEle = DocumentHelper.createElement("term");
//        parentEle.add(termEle);
//        // 根据这个token来判断下来做的事情
//        vmparse.CompilationEngine.Token curToken = getToken();
//        copyElement(termEle);
//        String text = curToken.getText();
//        String type = curToken.getName();
//        if (type.equals(TokenTypeEnum.IDENTIFIER.getCode())) {
//            // 如  a() a.b() a[i]
//            vmparse.CompilationEngine.Token nextToken = getToken();
//            if ("(".equals(nextToken.getText()) || ".".equals(nextToken.getText())) {
//                this.compilePartCall(termEle);
////                copyElement(termEle);
//            } else if ("[".equals(nextToken.getText())) {
//                // 复制 [
//                copyElement(termEle);
//                compileExpression(termEle);
//                if ("]".equals(getToken().getText())) {
//                    copyElement(termEle);
//                } else {
//                    System.out.println("遇到了非法的字符");
//                }
////                copyElement(termEle);
//            } else {
////                copyElement(termEle);
//                //                nodeIndex--;
//
//            }
//        } else if (type.equals(TokenTypeEnum.STRING_CONST.getCode()) ||
//                type.equals(TokenTypeEnum.INT_CONST.getCode())) {
////            copyElement(termEle);
//        } else {
//            if ("-".equals(text) || "~".equals(text)) {
//                compileTerm(termEle);
//            } else if ("null".equals(text)
//                    || "false".equals(text)
//                    || "true".equals(text)
//                    || "this".equals(text)
//            ) {
//                //TODO 这里
//                //刚才已经copy过了
////                copyElement(termEle);
//            } else if ("(".equals(text)) {
//                compileExpression(termEle);
//                if (")".equals(getToken().getText())) {
//                    copyElement(termEle);
//                } else {
//                    System.out.println("can not get )");
//                }
//            } else {
//                System.out.println("term get unExpect symbol");
//            }
//        }
    }

    private void compilePartCall(Element parentEle) {
//        vmparse.CompilationEngine.Token token = getToken();
//        String text = token.getText();
//        if ("(".equals(text)) {
//            copyElement(parentEle);
//            compileExpressionList(parentEle);
//            if (")".equals(getToken().getText())) {
//                copyElement(parentEle);
//            } else {
//                System.out.println("error is compilePartCall miss )");
//            }
//        } else if (".".equals(text)) {
//            copyElement(parentEle);
//            if (!TokenTypeEnum.IDENTIFIER.getCode().equals(getToken().getName())) {
//                System.out.println("error is compilePartCall not IDENTIFIER");
//            }
//            copyElement(parentEle);
//            if (!"(".equals(getToken().getText())) {
//                System.out.println("error is compilePartCall not has (");
//            }
//            copyElement(parentEle);
//            compileExpressionList(parentEle);
//            if (!")".equals(getToken(nodeIndex).getText())) {
//                System.out.println("error is compilePartCall not has )");
//            }
//            copyElement(parentEle);
//        }
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
//        parentEle.add(getElementByIndex(nodeIndex++));
    }

    private void copyElement(Element parentEle, int num) {
        for (int i = 0; i < num; i++) {
            copyElement(parentEle);
        }
    }


    private TokenTypeEnum getTokenType() {
        return tokens.get(tokenIndex).getType();
    }

    private String getTokenText() {
        return tokens.get(tokenIndex).getText();
    }

    private Integer getTokenLineNum() {
        return tokens.get(tokenIndex).getLineNum();
    }

}

