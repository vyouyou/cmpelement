package vmfinal;

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

    public VMWriter(String fileName) {
        File targetFile = new File(fileName);
        try {
            targetFile.createNewFile();
            writer = new BufferedWriter(new FileWriter(targetFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writePush(Constants.MemoryKindEnum memoryKind, int index) {
        writeToFile("push " + memoryKind.getCode() + " " + index);
    }

    public void writePop(Constants.MemoryKindEnum memoryKind, int index) {
        writeToFile("pop " + memoryKind.getCode() + " " + index);
    }

    /**
     * 算数运算符
     *
     * @param arithmetic
     */
    public void writeArithmetic(Constants.Arithmetic arithmetic) {
        writeToFile(arithmetic.getCode());
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
        writeToFile("function " + name + " " + nArgs);
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
            writer.write(code + "\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
