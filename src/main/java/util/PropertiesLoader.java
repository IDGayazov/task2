package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class for loading properties from a properties file.
 */
public class PropertiesLoader {

    private Properties properties;

    public PropertiesLoader(String propertiesFileName) {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader()
                                           .getResourceAsStream(propertiesFileName)) {

            if (input == null) {
                throw new IOException("Properties file not found: " + propertiesFileName);
            }

            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}