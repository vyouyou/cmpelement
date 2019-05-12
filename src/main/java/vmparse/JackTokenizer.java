package vmparse;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import utils.FileUtils;
import vmparse.constants.Constants;
import vmparse.constants.KeyWordEnum;
import vmparse.constants.TokenTypeEnum;

import java.io.*;
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

    public JackTokenizer(String path) {
        try {
            List<String> stringList = FileUtils.readFile(path);
            genXml(genTokens(stringList));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> genTokens(List<String> stringList) {
        List<String> tokens = Lists.newArrayList();
        stringList.stream().forEach((item) -> {
            for (String s : item.split(Constants.SYMBOL_SPLIT_PATTERN)) {
                Matcher matcher = STR_P.matcher(s);
                if (matcher.find()) {
                    tokens.add(s);
                } else {
                    tokens.addAll(Lists.newArrayList(s.split(" ")).stream().filter((str) -> {
                        str = str.trim();
                        return StringUtils.isNotEmpty(str);
                    }).collect(Collectors.toList()));
                }
            }
        });
        return tokens;
    }

    private void genXml(List<String> tokens) {
        Document document = DocumentHelper.createDocument();
        Element tokensEle = document.addElement("tokens");
        tokens.forEach((token) -> {
            insertDom(tokensEle, token, getTokenType(token));
        });
        FileUtils.writeIntoXml("fss.xml", document);
    }

    private void insertDom(Element rootElement, String token, TokenTypeEnum tokenTypeEnum) {
        Element e = rootElement.addElement(tokenTypeEnum.getCode());
        if (tokenTypeEnum.equals(TokenTypeEnum.STRING_CONST)) {
            token = token.replaceAll("\"", "");
            e.setText(token);
        }
        e.setText(token);
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
