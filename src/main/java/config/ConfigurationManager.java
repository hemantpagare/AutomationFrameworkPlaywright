package config;

import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigurationManager.class.getClassLoader().getResourceAsStream("framework.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load framework.properties", e);
        }
    }

    public static String get(String key) {
        // VM arguments take precedence over properties file
        return System.getProperty(key, properties.getProperty(key));
    }
}