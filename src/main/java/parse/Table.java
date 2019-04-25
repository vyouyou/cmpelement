package parse;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author qisy01
 * @create 19-4-24
 * @since 1.0.0
 */
public class Table {
    private Map<String, Integer> table;

    private int ramAddress = 16;

    Table() {
        table = Maps.newHashMap();
        table.put("SP", 0);
        table.put("LCL", 1);
        table.put("ARG", 2);
        table.put("THIS", 3);
        table.put("THAT", 4);
        table.put("SCREEN", 16384);
        table.put("KBD", 24567);
        for (int i = 0; i < 16; i++) {
            table.put("R" + i, i);
        }
    }

    public void addEntry(String symbol, int address) {
        table.put(symbol, address);
    }

    public boolean contains(String symbol) {
        return table.containsKey(symbol);
    }

    public int getAddress(String symbol) {
        return table.get(symbol);
    }

    public void setRamAddress(int ramAddress) {
        this.ramAddress = ramAddress;
    }

    public int getRamAddress() {
        return ramAddress;
    }
}
