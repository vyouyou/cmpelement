package parse;

import javafx.scene.control.Tab;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.List;

/**
 * @author qisy01
 * @create 19-4-21
 * @since 1.0.0
 */
public class Parser {

    private String path;

    private int lineNum;

    private Table table;

    public Parser(String path) {
        this.path = path;
        lineNum = -1;
        table = new Table();
    }

    private List<String> readFile() throws IOException {
        return FileUtils.readLines(new File(path), "UTF-8");
    }

    public void parse() {
        try {
            List<String> commands = readFile();
            commands.stream().forEach(command -> {
                CommandTypeEnum type = commandType(command);
                switch (type) {
                    case C_COMMNAD:
                        lineNum++;
                        break;
                    case A_COMMAND:
                        lineNum++;
                        break;
                    case L_COMMAND:
                        table.addEntry(symbol(command, type), lineNum + 1);
                    default:
                }
            });
            StringBuilder sb = new StringBuilder();
            commands.stream().forEach(command -> {
                CommandTypeEnum type = commandType(command);
                switch (type) {
                    case C_COMMNAD:
                        try {
                            sb
                                    .append("111")
                                    .append(Code.comp(command))
                                    .append(Code.dest(command))
                                    .append(Code.jump(command))
                                    .append("\r\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case A_COMMAND:
                        String token = symbol(command, type);
                        StringBuilder binary = new StringBuilder();
                        if (StringUtils.isNumeric(token)) {
                            binary.append(int2Binary(Integer.parseInt(token)));
                        } else {
                            if (table.contains(token)) {
                                binary.append(int2Binary(table.getAddress(token)));
                            } else {
                                int ramAddress = table.getRamAddress();
                                binary.append(int2Binary(ramAddress));
                                table.addEntry(token, ramAddress++);
                            }
                        }
                        sb.append(binary + "\r\n");
                    default:
                }
            });
            System.out.println(sb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CommandTypeEnum commandType(String command) {
        if (command.startsWith("@")) {
            return CommandTypeEnum.A_COMMAND;
        }
        if (command.startsWith("(")) {
            return CommandTypeEnum.L_COMMAND;
        } else {
            return CommandTypeEnum.C_COMMNAD;
        }
    }

    public String symbol(String command, CommandTypeEnum typeEnum) {
        if (typeEnum.equals(CommandTypeEnum.A_COMMAND)) {
            return StringUtils.remove(command, "@");
        } else {
            return StringUtils.remove(command, "/^\\((.+)\\)$/");
        }
    }


    private String int2Binary(int i) {
        String str = Integer.toBinaryString(i);
        return String.format("%16s", str).replace(" ", "0");
    }
}
