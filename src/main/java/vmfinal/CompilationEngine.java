package vmfinal;

import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import vmparse.constants.KeyWordEnum;
import vmparse.constants.TokenTypeEnum;

import java.util.List;
import java.util.Optional;
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
    /**
     * while label 第几个
     */
    private int whileLabelIndex = 0;

    private final static String WHILE_LABEL = "WHILE_LABEL_";

    private final static String WHILE_LABEL_END = "WHILE_LABEL_END_";
    /**
     * if label 第几个
     */
    private int ifTrueIndex = 0;
    /**
     * if false label
     */
    private int ifFalseIndex = 0;
    /**
     * if end label
     */
    private int ifEndIndex = 0;

    private final static String IF_TRUE = "IF_TRUE_";

    private final static String IF_FALSE = "IF_FALSE_";

    private final static String IF_END = "IF_END_";
    /**
     * 当前方法类型  constructor method  function
     */
    private String functionType;


    public CompilationEngine(List<TokenWithLineNumber> tokens, String target) {
        symbolTableStack = new Stack<>();
        this.tokens = tokens;
        this.vmWriter = new VMWriter(target);
    }

    public void startParse() {
        compileClass();
        compileClassVarDec();
        String nextTokenText = getNextTokenText();
        while (KeyWordEnum.CONSTRUCTOR.getCode().equals(nextTokenText) ||
                KeyWordEnum.FUNCTION.getCode().equals(nextTokenText) ||
                KeyWordEnum.METHOD.getCode().equals(nextTokenText)
        ) {
            functionType = nextTokenText;
            compileSubroutine(nextTokenText);
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
    }

    /**
     * 编译静态变量或者字段
     */
    private void compileClassVarDec() {
        SymbolTable classSymbolTable = new SymbolTable();
        tokenIndex++;
        while (KeyWordEnum.STATIC.getCode().equals(getTokenText())
                || KeyWordEnum.FIELD.getCode().equals(getTokenText())) {
            String keyword = getTokenText();
            String type = getNextTokenText();
            tokenIndex++;
            while (true) {
                String identify = getTokenText();
                classSymbolTable.define(identify, type, vmfinal.Constants.SymbolKindEnum.getByCode(keyword));
                tokenIndex++;
                String nextText = getTokenText();
                if (",".equals(nextText)) {
                    tokenIndex++;
                }
                if (";".equals(nextText)) {
                    tokenIndex++;
                    break;
                }
            }
        }
        symbolTableStack.push(classSymbolTable);
        tokenIndex--;
    }

    /**
     * 编译方法
     */
    private void compileSubroutine(String tokenText) {
        tokenIndex += 2;
        String methodName = getTokenText();
        addArgs();
        // 处理  {
        tokenIndex++;
        int argsNum = compileVar();
        vmWriter.writeFunction(methodName, argsNum);
        if (tokenText.equals(KeyWordEnum.CONSTRUCTOR.getCode())) {
            int fieldNum = symbolTableStack.firstElement().switchIndexByKind(Constants.SymbolKindEnum.FIELD);
            vmWriter.writePush(Constants.MemoryKindEnum.CONST, fieldNum);
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(Constants.MemoryKindEnum.POINTER, 0);
        } else if (tokenText.equals(KeyWordEnum.METHOD.getCode())) {
            vmWriter.writePush(Constants.MemoryKindEnum.ARG, 0);
            vmWriter.writePop(Constants.MemoryKindEnum.POINTER, 0);
        }
        compileStatementList();
        /**
         *  function int nextMask(int mask) {
         *     	if (mask = 0) {
         *     	    return 1;
         *                }
         *     	else {
         * 	    return mask * 2;
         *        }
         *     }
         *     遇到了形如这样的 会读到 } 所以加上此段代码
         */
        //TODO 待优化
        while (true) {
            if (!"}".equals(getNextTokenText())) {
                tokenIndex--;
                break;
            }
        }
    }

    private void addArgs() {
        SymbolTable tempTable;
        if (symbolTableStack.size() == 1) {
            tempTable = new SymbolTable();
            symbolTableStack.push(tempTable);
        } else {
            tempTable = symbolTableStack.peek();
            tempTable.startSubroutine();
        }
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
        vmWriter.writePop(Constants.MemoryKindEnum.TEMP, 0);

    }

    /**
     * let
     */
    private void compileLet() {
        String argName = getNextTokenText();
        if ("[".equals(getNextTokenText())) {
            tokenIndex++;
            compileExpression();
            if ("]".equals(getNextTokenText()) && getSymbolTypeKind(argName) != null) {
                SymbolTable.SymbolTypeKind symbolTypeKind = getSymbolTypeKind(argName);
                vmWriter.writePush(memoryKindMap(symbolTypeKind.getKind()), symbolTypeKind.getOrder());
                vmWriter.writeArithmetic("+");
                tokenIndex += 2;
                compileExpression();
                vmWriter.writePop(Constants.MemoryKindEnum.TEMP, 0);
                vmWriter.writePop(Constants.MemoryKindEnum.POINTER, 1);
                vmWriter.writePush(Constants.MemoryKindEnum.TEMP, 0);
                vmWriter.writePop(Constants.MemoryKindEnum.THAT, 0);
            }
        } else {
            getNextTokenText();
            compileExpression();
            pop2Var(argName);
        }
        // TODO 这里是一个bug 因为最后可能 当为 n+1这种，最后读不到；
        if (getNextTokenText().equals(";")) {
            tokenIndex++;
        }
    }

    /**
     * var
     */
    private int compileVar() {
        SymbolTable methodSymbol = symbolTableStack.peek();
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
    private void compileWhile() {
        String startLabel = WHILE_LABEL + whileLabelIndex;
        String endLabel = WHILE_LABEL_END + whileLabelIndex;
        vmWriter.writeLabel(startLabel);
        tokenIndex += 2;
        compileExpression();
        vmWriter.writeNumberDecorater("~");
        vmWriter.writeIf(endLabel);
        tokenIndex += 3;
        compileStatementList();
        vmWriter.writeGoto(startLabel);
        vmWriter.writeLabel(endLabel);
        tokenIndex += 2;
        whileLabelIndex++;
    }

    /**
     * return
     */
    private void compileReturn() {
        if (";".equals(getNextTokenText())) {
            vmWriter.writePush(Constants.MemoryKindEnum.CONST, 0);
            tokenIndex++;
        } else {
            compileExpression();
            tokenIndex++;
        }
        vmWriter.writeReturn();
        tokenIndex++;
        if (";".equals(getTokenText())) {
            tokenIndex++;
        }
    }

    /**
     * if
     */
    private void compileIf() {
        String ifTagTextStart = IF_TRUE + ifTrueIndex++;
        String ifTagTextFalse = IF_FALSE + ifFalseIndex++;
        tokenIndex += 2;
        compileExpression();
        // 处理 )
        tokenIndex++;
        vmWriter.writeIf(ifTagTextStart);
        vmWriter.writeGoto(ifTagTextFalse);
        tokenIndex += 2;
        vmWriter.writeLabel(ifTagTextStart);
        compileStatementList();
        // 处理}
        tokenIndex++;
        if ("else".equals(getNextTokenText())) {
            // 如果有else，则需要有跳过else这一段的代码
            String endTagText = IF_END + ifEndIndex++;
            vmWriter.writeGoto(endTagText);
            vmWriter.writeLabel(ifTagTextFalse);
            // 处理 { 并且将指针至于开头
            tokenIndex += 2;
            compileStatementList();
            tokenIndex += 2;
            vmWriter.writeLabel(endTagText);
        } else {
            vmWriter.writeLabel(ifTagTextFalse);
        }
    }


    /**
     * 表达式列表 ，用于方法调用中 fun(a,b)
     */
    private void compileStatementList() {
        while (true) {
            String tokenText = getTokenText();
            if (KeyWordEnum.DO.getCode().equals(tokenText)) {
                compileDo();
            } else if (KeyWordEnum.LET.getCode().equals(tokenText)) {
                compileLet();
            } else if (KeyWordEnum.IF.getCode().equals(tokenText)) {
                compileIf();
            } else if (KeyWordEnum.WHILE.getCode().equals(tokenText)) {
                compileWhile();
            } else if (KeyWordEnum.RETURN.getCode().equals(tokenText)) {
                compileReturn();
            } else {
                // 当statement结束后 停止在 } 上 因为获取下个方法会用 getNextToken
                tokenIndex--;
                break;
            }
        }
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
            String mathSymbol = getTokenText();
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
                tokenIndex--;
                this.compilePartCall(text);
//                copyElement(termEle);
            } else if ("[".equals(nextText)) {
                tokenIndex++;
                compileExpression();
                tokenIndex++;
                if ("]".equals(getNextTokenText())) {
                    SymbolTable.SymbolTypeKind kind = getSymbolTypeKind(text);
                    vmWriter.writePush(memoryKindMap(kind.getKind()), kind.getOrder());
                    vmWriter.writeArithmetic("+");
                    vmWriter.writePop(Constants.MemoryKindEnum.POINTER, 1);
                    vmWriter.writePush(Constants.MemoryKindEnum.THAT, 0);
                } else {

                }

            }
            // 处理变量
            else {
                push2Var(text);
            }
        } else if (type.equals(TokenTypeEnum.KEYWORD)) {
            if ("null".equals(text) || "false".equals(text)) {
                vmWriter.writePush(Constants.MemoryKindEnum.CONST, 0);
            }
            if ("true".equals(text)) {
                vmWriter.writePush(Constants.MemoryKindEnum.CONST, 0);
                vmWriter.writeNumberDecorater("~");
            }
            // 处理this关键字
            if ("this".equals(text)) {
                vmWriter.writePush(Constants.MemoryKindEnum.POINTER, 0);
            }
            tokenIndex++;
        }
        // 处理int类型
        else if (type.equals(TokenTypeEnum.INT_CONST)) {
            vmWriter.writePush(Constants.MemoryKindEnum.CONST, getTokenText());
            tokenIndex++;
        }
        // 处理string类型
        else if (type.equals(TokenTypeEnum.STRING_CONST)) {
            char[] charArray = text.toCharArray();
            vmWriter.writePush(Constants.MemoryKindEnum.CONST, charArray.length);
            vmWriter.writeCall("String.new", 1);
            for (char c : charArray) {
                vmWriter.writePush(Constants.MemoryKindEnum.CONST, (byte) c);
                vmWriter.writeCall("String.appendChar", 2);
            }
            tokenIndex++;
        } else if ("-".equals(text) || "~".equals(text)) {
            tokenIndex++;
            compileTerm();
            vmWriter.writeNumberDecorater(text);
        } else if ("(".equals(text)) {
            tokenIndex++;
            compileExpression();
            if (")".equals(getNextTokenText())) {
                tokenIndex++;
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
            vmWriter.writePush(Constants.MemoryKindEnum.POINTER, 0);
            funcName = className + "." + funcName;
            argsNum = compileExpressionList() + 1;
        } else if (".".equals(text)) {
            SymbolTable.SymbolTypeKind kind = getSymbolTypeKind(funcName);
            // 说明调用 的是一个static方法
            if (kind == null) {
                funcName = funcName + "." + getNextTokenText();
            } else {
                vmWriter.writePush(memoryKindMap(kind.getKind()), kind.getOrder());
                funcName = kind.getType() + "." + getNextTokenText();
            }
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

    private SymbolTable.SymbolTypeKind getSymbolTypeKind(String name) {
        SymbolTable topTable = symbolTableStack.peek();
        SymbolTable bottomTable = symbolTableStack.firstElement();
        SymbolTable.SymbolTypeKind kind = topTable.getByName(name);
        if (kind == null) {
            kind = bottomTable.getByName(name);
        }
        return kind;
    }

    private void pop2Var(String name) {
        SymbolTable.SymbolTypeKind kind = getSymbolTypeKind(name);
        vmWriter.writePop(memoryKindMap(kind.getKind()), kind.getOrder());
    }

    private void push2Var(String name) {
        SymbolTable.SymbolTypeKind kind = getSymbolTypeKind(name);
        vmWriter.writePush(memoryKindMap(kind.getKind()), kind.getOrder());
    }

    private TokenTypeEnum getTokenType() {
        return tokens.get(tokenIndex).getType();
    }

    private String getTokenText(int offset) {
        return tokens.get(tokenIndex + offset).getText();
    }

    private String getTokenText() {
        return tokens.get(tokenIndex).getText();
    }

    private String getNextTokenText() {
        if (tokenIndex == tokens.size() - 1) {
            return "";
        }
        return tokens.get(++tokenIndex).getText();
    }

    private Integer getTokenLineNum() {
        return tokens.get(tokenIndex).getLineNum();
    }

    private Constants.MemoryKindEnum memoryKindMap(Constants.SymbolKindEnum symbolKind) {
        switch (symbolKind) {
            case VAR:
                return Constants.MemoryKindEnum.LOCAL;
            case ARG:
                return Constants.MemoryKindEnum.ARG;
            case STATIC:
                return Constants.MemoryKindEnum.STATIC;
            case FIELD:
                return Constants.MemoryKindEnum.THIS;
        }
        return null;
    }

}

