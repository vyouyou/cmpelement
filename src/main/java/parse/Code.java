package parse;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

/**
 * @author qisy01
 * @create 19-4-21
 * @since 1.0.0
 */
public class Code {
    private static HashMap<String, String> CSet;

    static {
        CSet = Maps.newHashMap();
        CSet.put("0", "0101010");
        CSet.put("1", "0111111");
        CSet.put("-1", "0111010");
        CSet.put("D", "0001100");
        CSet.put("A", "0110000");
        CSet.put("M", "1110000");
        CSet.put("!D", "0001101");
        CSet.put("!A", "0110001");
        CSet.put("!M", "1110001");
        CSet.put("-D", "0001111");
        CSet.put("-A", "0110011");
        CSet.put("-M", "1110011");
        CSet.put("D+1", "0011111");
        CSet.put("A+1", "0110111");
        CSet.put("M+1", "1110111");
        CSet.put("D-1", "0001110");
        CSet.put("A-1", "0110010");
        CSet.put("M-1", "1110010");
        CSet.put("D+A", "0000010");
        CSet.put("D+M", "1000010");
        CSet.put("D-A", "0010011");
        CSet.put("D-M", "1010011");
        CSet.put("A-D", "0000111");
        CSet.put("M-D", "1000111");
        CSet.put("D&A", "0000000");
        CSet.put("D&M", "1000000");
        CSet.put("D|A", "0010101");
        CSet.put("D|M", "1010101");
    }

    public static HashMap<String, String> getCSet() {
        return CSet;
    }

    public enum JSet {
        JGT("JGT", "001"),
        JEQ("JEQ", "010"),
        JGE("JGE", "011"),
        JLT("JLT", "100"),
        JNE("JNE", "101"),
        JLE("JLE", "110"),
        JMP("JMP", "111");

        private String name;
        private String code;

        JSet(String name, String code) {
            this.name = name;
            this.code = code;
        }

        public static String get(String code) {
            return JSet.valueOf(StringUtils.upperCase(code)).code;
        }
    }

    public static final String dest(String command) {
        char[] results = {'0', '0', '0'};
        if (command.indexOf(Constants.EQUALS_SYM) > 0) {
            String[] strs = command.split(Constants.EQUALS_SYM);
            String dest = strs[0];
            for (String s : dest.split("")) {
                switch (s) {
                    case "A":
                        results[0] = '1';
                        break;
                    case "D":
                        results[1] = '1';
                        break;
                    case "M":
                        results[2] = '1';
                        break;
                    default:
                        break;
                }
            }
        }
        return new String(results);
    }

    public static final String comp(String command) throws Exception {
        if (command.indexOf(Constants.EQUALS_SYM) > 0) {
            command = command.split(Constants.EQUALS_SYM)[1];
        }
        if (command.indexOf(Constants.PART_SYM) > 0) {
            command = command.split(Constants.PART_SYM)[0];
        }
        command = command.replace(" ", "");
        String binary = CSet.get(command);
        if (StringUtils.isNotEmpty(binary)) {
            return binary;
        } else {
            if (StringUtils.containsAny(command, "+", "|", "&")) {
                command = StringUtils.reverse(command);
                binary = CSet.get(command);
                return binary;
            }
        }
        throw new Exception();
    }

    public static final String jump(String command) {
        if (StringUtils.contains(command, Constants.PART_SYM)) {
            return JSet.get(StringUtils.split(command, Constants.PART_SYM)[1]);
        }
        return "000";
    }

}
