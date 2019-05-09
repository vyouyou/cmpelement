package vmparse;

import com.google.common.collect.Lists;
import com.sun.xml.internal.bind.v2.runtime.reflect.opt.Const;
import jdk.internal.util.xml.XMLStreamException;
import jdk.nashorn.internal.parser.TokenType;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
            for (String s : item.split(Constants.SYMBOL_PATTERN)) {
                tokens.addAll(Lists.newArrayList(s.split(" ")).stream().filter((str) -> {
                    str = str.trim();
                    return StringUtils.isNotEmpty(str);
                }).collect(Collectors.toList()));
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
        File file = new File("rss.xml");
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8");
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.setEscapeText(false);
            writer.write(document);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertDom(Element rootElement, String token, TokenTypeEnum tokenTypeEnum) {
        Element e = rootElement.addElement(tokenTypeEnum.getCode());
        if (tokenTypeEnum.equals(TokenTypeEnum.STRING_CONST)) {
            token = token.replaceAll("\"", "");
            e.setText(token);
        }
        e.setText(token);
    }

    public static void main(String[] args) {
        System.out.println("\"aaa\"".replaceAll("\"",""));
    }

    private TokenTypeEnum getTokenType(String str) {
        if (str.matches(Constants.SYMBOL_PATTERN)) {
            return TokenTypeEnum.SYMBOL;
        }
        if (str.matches(Constants.IDENTIFY_PATTERN)) {
            return TokenTypeEnum.IDENTIFIER;
        }
        if (str.matches(Constants.KEYWORD_PATTERN)) {
            return TokenTypeEnum.KEYWORD;
        }
        if (str.matches(Constants.STRING_PATTERN)) {
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
