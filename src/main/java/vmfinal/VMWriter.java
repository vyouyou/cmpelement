package vmfinal;

/**
 * @Author qishiyu
 * @create 2019/5/27 21:08
 */
public class VMWriter {
    public VMWriter() {
    }

    private void writePush(Constants.MemoryKindEnum memoryKind, int index) {
    }

    private void writePop(Constants.MemoryKindEnum memoryKind, int index) {
    }

    /**
     * 算数运算符
     *
     * @param arithmetic
     */
    private void writeArithmetic(Constants.Arithmetic arithmetic) {
    }

    private void writeLabel(String label) {
    }

    private void writeGoto(String label) {
    }

    private void writeIf(String label) {
    }

    private void writeCall(String name, int nArgs) {
    }

    private void writeFunction(String name, int nArgs) {
    }

    private void writeReturn() {
    }

    private void close() {
    }
}
