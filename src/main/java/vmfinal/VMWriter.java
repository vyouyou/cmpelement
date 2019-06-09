package vmfinal;

import lombok.Setter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author qishiyu
 * @create 2019/5/27 21:08
 */
public class VMWriter {

    BufferedWriter writer;

    @Setter
    private String className;

    public VMWriter(String fileName) {
        File targetFile = new File(fileName);
        this.className = className;
        try {
            targetFile.createNewFile();
            writer = new BufferedWriter(new FileWriter(targetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writePop(Constants.MemoryKindEnum memoryKind, int index) {
        writeToFile("pop " + memoryKind.getCode() + " " + index);
    }

    public void writePop(Constants.MemoryKindEnum memoryKind, String index) {
        writeToFile("pop " + memoryKind.getCode() + " " + index);
    }

    public void writePush(Constants.MemoryKindEnum memoryKind, String index) {
        writeToFile("push " + memoryKind.getCode() + " " + index);
    }

    public void writePush(Constants.MemoryKindEnum memoryKind, int index) {
        writeToFile("push " + memoryKind.getCode() + " " + index);
    }

    /**
     * 算数运算符
     *
     * @param code
     */
    public void writeArithmetic(String code) {
        String symbolText = "";
        switch (code) {
            case "+":
                symbolText = "add";
                break;
            case "-":
                symbolText = "sub";
                break;
            case "*":
                symbolText = "call Math.multiply 2";
                break;
            case "/":
                symbolText = "call Math.divide 2";
                break;
            default:
                symbolText = "";
        }
        writeToFile(symbolText);
    }

    /**
     * 数字前面的修饰符  -   neg    ~ not
     *
     * @param label
     */
    public void writeNumberDecorater(String label) {
        switch (label) {
            case "-":
                writeToFile("neg");
                break;
            case "~":
                writeToFile("not");
                break;
        }
    }

    public void writeLabel(String label) {
        writeToFile(label);
    }

    public void writeGoto(String label) {
        writeToFile("goto " + label);
    }

    public void writeIf(String label) {
        writeToFile("if-goto " + label);
    }

    public void writeCall(String name, int nArgs) {
        writeToFile("call " + name + " " + nArgs);
    }

    public void writeFunction(String name, int nArgs) {
        writeToFile("function " + className + "." + name + " " + nArgs);
    }

    public void writeReturn() {
        writeToFile("return");
    }

    public void close() {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeToFile(String code) {
        try {
            writer.append(code + "\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
