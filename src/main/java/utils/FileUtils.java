package utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileUtils {
    public static List<String> readFile(String path) throws IOException {
        return org.apache.commons.io.FileUtils.readLines(new File(path), "UTF-8");
    }
}
