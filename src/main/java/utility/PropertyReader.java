package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {

    private Properties properties;

    //option 1
    public static String getValue(String filePath, String key) {
        File file = new File(filePath);
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
            return properties.getProperty(key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //option 2
    public PropertyReader(String filePath) {
        File file = new File(filePath);
        properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getValue(String key) {
        return properties.getProperty(key);
    }

}
