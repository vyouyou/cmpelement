package vmfinal;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Author qishiyu
 * @create 2019/5/27 21:09
 */
public class SymbolTable {

    private Map<String, SymbolTypeKind> symbolMap;

    private Integer varIndex = 0;
    private Integer argIndex = 0;
    private Integer staticIndex = 0;
    private Integer fieldIndex = 0;

    public SymbolTable() {
        symbolMap = Maps.newHashMap();
    }

    /**
     * 开启新的子程序，将table重置
     */
    public void startSubroutine() {
        symbolMap.clear();
        varIndex = 0;
        argIndex = 0;
        staticIndex = 0;
        fieldIndex = 0;
    }

    /**
     * 定义标识符并给索引
     *
     * @param name
     * @param type
     * @param kind
     */
    public void define(String name, String type, Constants.SymbolKindEnum kind) {
        int tempNum = 0;
        if (Constants.SymbolKindEnum.ARG.equals(kind)) {
            argIndex++;
            tempNum = argIndex;
        } else if (Constants.SymbolKindEnum.FIELD.equals(kind)) {
            fieldIndex++;
            tempNum = fieldIndex;
        } else if (Constants.SymbolKindEnum.STATIC.equals(kind)) {
            staticIndex++;
            tempNum = staticIndex;
        } else if (Constants.SymbolKindEnum.VAR.equals(kind)) {
            varIndex++;
            tempNum = varIndex;
        }
        symbolMap.put(name, new SymbolTypeKind(type, kind, tempNum - 1));
    }

    private int varCount(Constants.SymbolKindEnum kind) {
        return switchIndexByKind(kind);
    }

    private Constants.SymbolKindEnum kindOf(String name) {
        SymbolTypeKind typeKind = symbolMap.get(name);
        if (null == typeKind) {
            throw new CompileException("不存在该name");
        }
        return typeKind.getKind();
    }

    public String typeOf(String name) {
        SymbolTypeKind typeKind = symbolMap.get(name);
        if (null == typeKind) {
            throw new CompileException("不存在该name");
        }
        return typeKind.getType();
    }

    public SymbolTypeKind getByName(String name) {
        return symbolMap.get(name);
    }

    /**
     * 返回索引
     *
     * @param name
     * @return
     */
    private int indexOf(String name) {
        SymbolTypeKind typeKind = symbolMap.get(name);
        if (null == typeKind) {
            throw new CompileException("不存在该name");
        }
        return switchIndexByKind(typeKind.getKind());
    }

    public Integer switchIndexByKind(Constants.SymbolKindEnum kind) {
        if (Constants.SymbolKindEnum.ARG.equals(kind)) {
            return argIndex;
        } else if (Constants.SymbolKindEnum.FIELD.equals(kind)) {
            return fieldIndex;
        } else if (Constants.SymbolKindEnum.STATIC.equals(kind)) {
            return staticIndex;
        } else if (Constants.SymbolKindEnum.VAR.equals(kind)) {
            return varIndex;
        } else {
            throw new CompileException("获取到错误的kind");
        }
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SymbolTypeKind {
        /**
         * 类型
         */
        private String type;
        /**
         * 种类
         */
        private Constants.SymbolKindEnum kind;
        /**
         * 第几个
         */
        private int order;
    }
}
