package vmfinal;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import utils.FileUtils;
import vmparse.constants.Constants;
import vmparse.constants.KeyWordEnum;
import vmparse.constants.TokenTypeEnum;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static vmparse.constants.Constants.STR_P;

/**
 * @Author qishiyu
 * @create 2019/5/5 18:44
 */
public class JackTokenizer {

    @Getter
    List<TokenWithLineNumber> tokenWithLineNumberList;

    public JackTokenizer(String path) {
        try {
            List<String> stringList = FileUtils.readFile(path);
            stringList = removeComment(stringList);
            tokenWithLineNumberList = genTokens(stringList);
            genType(tokenWithLineNumberList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 去除注释
     *
     * @param stringList
     * @return
     */
    private List<String> removeComment(List<String> stringList) {
        return stringList.stream().filter(item -> {
            if (item.trim().startsWith("//") || item.trim().startsWith("/*")
                    || item.trim().startsWith("*")
                    || item.trim().startsWith("*/")) {
                return false;
            }
            return true;
        }).map(item -> {
            Pattern p = Pattern.compile("(\\/\\/).*");
            Matcher matcher = p.matcher(item);
            return matcher.replaceAll("");
        }).filter((item) -> StringUtils.isNotEmpty(item)
        ).collect(Collectors.toList());
    }

    private List<TokenWithLineNumber> genTokens(List<String> stringList) {
        List<String> tokens = Lists.newArrayList();
        List<TokenWithLineNumber> lineNumberTokenList = Lists.newArrayList();
        for (int i = 0; i < stringList.size(); i++) {
            String item = stringList.get(i);
            for (String s : item.split(Constants.SYMBOL_SPLIT_PATTERN)) {
                Matcher matcher = STR_P.matcher(s);
                if (matcher.find()) {
                    lineNumberTokenList.add(new TokenWithLineNumber(s, i));
                    tokens.add(s);
                } else {
                    int finalI = i;
                    Lists.newArrayList(s.split(" ")).stream().forEach((str) -> {
                        str = str.trim();
                        if (StringUtils.isNotEmpty(str)) {
                            lineNumberTokenList.add(new TokenWithLineNumber(str, finalI));
                        }
                    });
                }
            }
        }
        return lineNumberTokenList;
    }

    private void genType(List<TokenWithLineNumber> tokens) {
        tokens.forEach((token) -> {
            String text = token.getText();
            TokenTypeEnum tokenType = getTokenType(text);
            if (TokenTypeEnum.STRING_CONST.equals(tokenType)) {
                token.setText(text.replaceAll("\"", ""));
            }
            token.setType(tokenType);
        });
    }


    private TokenTypeEnum getTokenType(String str) {
        if (str.matches(Constants.KEYWORD_PATTERN)) {
            return TokenTypeEnum.KEYWORD;
        }
        if (str.matches(Constants.SYMBOL_PATTERN)) {
            return TokenTypeEnum.SYMBOL;
        }
        if (str.matches(Constants.IDENTIFY_PATTERN)) {
            return TokenTypeEnum.IDENTIFIER;
        }
        if (STR_P.matcher(str).find()) {
            return TokenTypeEnum.STRING_CONST;
        }
        if (str.matches(Constants.INTEGER_PATTERN)) {
            return TokenTypeEnum.INT_CONST;
        }
        return TokenTypeEnum.SYMBOL;
    }

    private KeyWordEnum getKeyWord(TokenTypeEnum tokenTypeEnum, String str) {
        return null;
    }

}
