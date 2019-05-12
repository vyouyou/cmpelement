package utils;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.*;
import java.util.List;

public class FileUtils {
    public static List<String> readFile(String path) throws IOException {
        return org.apache.commons.io.FileUtils.readLines(new File(path), "UTF-8");
    }

    public static void writeIntoXml(String filePath, Document document) {
        File file = new File(filePath);
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
}
