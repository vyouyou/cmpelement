package vmfinal;

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

    public void startParse() {
        compileClass();
        compileClassVarDec();
        String nextTokenText = getNextTokenText();
        while (KeyWordEnum.CONSTRUCTOR.getCode().equals(nextTokenText) ||
                KeyWordEnum.FUNCTION.getCode().equals(nextTokenText) ||
                KeyWordEnum.METHOD.getCode().equals(nextTokenText)
        ) {
            compileSubroutine();
            nextTokenText = getNextTokenText();
        }
        vmWriter.close();
    }

    /**
     * 编译整个类
     */
    private void compileClass() {
        if ("class".equals(getTokenText())) {
            className = getNextTokenText();
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
            String type = getNextTokenText();
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
        tokenIndex += 2;
        String methodName = getTokenText();
        addArgs();
        // 处理  {
        tokenIndex++;
        int argsNum = compileVar();
        vmWriter.writeFunction(methodName, argsNum);
        compileStatementList();
    }

    private void addArgs() {
        SymbolTable tempTable = new SymbolTable();
        symbolTableStack.push(tempTable);
        tokenIndex++;
        // (
        while (true) {
            if (")".equals(getNextTokenText())) {
                break;
            }
            if (",".equals(getTokenText())) {
                tokenIndex++;
            }
            String type = getTokenText();
            String name = getNextTokenText();
            tempTable.define(name, type, Constants.SymbolKindEnum.ARG);
            tokenIndex++;
        }
        //)
        tokenIndex++;
    }


    /**
     * 编译参数列表 声明方法后面的一系列参数
     */
    private int compileParameterList() {
        tokenIndex++;
        int number = 0;
        while (true) {
            String text = getNextTokenText();
            if (text.equals(")")) {
                break;
            } else if (text.equals(",")) {
                tokenIndex++;
            }
            number++;
            compileTerm();
        }
        return number;
    }


    /**
     * do
     * do aaa(exp1,exp2)
     */
    private void compileDo() {
        // 先获取方法名称
        String funcName = getNextTokenText();
        compilePartCall(funcName);
        // 处理  ;
        tokenIndex++;
        // 对于 do  要将结果弹出
        vmWriter.writePop(Constants.MemoryKindEnum.TEMP, 0
        );
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
        compileExpression();
        // )  }
        copyElement(whileEle, 2);
        compileStatementList();
        // }
        copyElement(whileEle);
    }

    /**
     * return
     */
    private void compileReturn() {
        if (";".equals(getNextTokenText())) {
            vmWriter.writePush(Constants.MemoryKindEnum.CONST, 0);
            tokenIndex++;
        }
        vmWriter.writeReturn();
    }

    /**
     * if
     */
    private void compileIf(Element parentEle) {
        Element ifEle = createKeywordElement(KeyWordEnum.IF);
        parentEle.add(ifEle);
        // if  (
        copyElement(ifEle, 2);
        compileExpression();
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
        while (true) {
            String tokenText = getTokenText();
            if (KeyWordEnum.DO.getCode().equals(tokenText)) {
                compileDo();
            } else if (KeyWordEnum.RETURN.getCode().equals(tokenText)) {
                compileReturn();
            } else {
                break;
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

    private int compileExpressionList() {
        int expressSize = 0;
        while (!")".equals(getNextTokenText())) {
            tokenIndex++;
            compileExpression();
            expressSize++;
        }
        return expressSize;
    }

    /**
     * 表达式 let i = (1 + 2 ) 后面为 expression
     */
    private void compileExpression() {
        compileTerm();
        while (true) {
            String mathSymbol = getNextTokenText();
            if (!Constants.MATH_PATTERN.contains(mathSymbol)) {
                tokenIndex--;
                break;
            }
            tokenIndex++;
            compileTerm();
            vmWriter.writeArithmetic(mathSymbol);
        }
    }

    /**
     * 用于expression中的 参数 ( 1 + 2) 则 term  1
     * a.b(<explist>c,d,e</explist>)
     * [ <term>i</term> ]
     * <term>a.b(c)</term>
     */
    private void compileTerm() {
        String text = getTokenText();
        TokenTypeEnum type = getTokenType();
        if (type.equals(TokenTypeEnum.IDENTIFIER)) {
            // 如  a() a.b() a[i]
            String nextText = getNextTokenText();
            if ("(".equals(nextText) || ".".equals(nextText)) {
                this.compilePartCall(text);
//                copyElement(termEle);
            } else if ("[".equals(nextText)) {
                compileExpression();
                if ("]".equals(getNextTokenText())) {
                } else {
                }
            }
        } else if (type.equals(TokenTypeEnum.STRING_CONST) ||
                type.equals(TokenTypeEnum.INT_CONST)) {
            vmWriter.writePush(Constants.MemoryKindEnum.CONST, getTokenText());
        } else if ("-".equals(text) || "~".equals(text)) {
            compileTerm();
        } else if ("(".equals(text)) {
            tokenIndex++;
            compileExpression();
            if (")".equals(getNextTokenText())) {
            } else {
                System.out.println("can not get )");
            }
        } else {
            System.out.println("term get unExpect symbol");
        }
    }

    private void compilePartCall(String funcName) {
        String text = getNextTokenText();
        int argsNum = 0;
        if ("(".equals(text)) {
            vmWriter.writePush(Constants.MemoryKindEnum.THIS, 2);
            argsNum = compileExpressionList() + 1;
        } else if (".".equals(text)) {
            funcName = funcName + "." + getNextTokenText();
            argsNum = compileExpressionList();
        }
        vmWriter.writeCall(funcName, argsNum);
        // 处理后括号 )
        tokenIndex++;
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

    private String getNextTokenText() {
        return tokens.get(++tokenIndex).getText();
    }

    private Integer getTokenLineNum() {
        return tokens.get(tokenIndex).getLineNum();
    }

}

