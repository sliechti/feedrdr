package feedreader.utils;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for loading .properties from current classpath with type safe getters.
 */
public class ApplicationConfig {

    private static ApplicationConfig instance = null;
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private Configuration config = null;

    private ApplicationConfig() {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(
                PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName("application.properties"));
        try {
            config = builder.getConfiguration();
        } catch (ConfigurationException e) {
            logger.error("failed to load application.properties: {}", e, e.getMessage());
        }
    }

    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public boolean getBoolean(String key, Boolean def) {
        return config.getBoolean(key, def);
    }

    public int getInt(String key) {
        int ret = config.getInt(key, -1);
        if (ret == -1) {
            logger.warn("int property not found: {}", key);
        }
        return ret;
    }

    public String getString(String key) {
        String ret = getString(key, "");
        if (ret.isEmpty()) {
            logger.warn("string proprety not found: {}", key);
        }
        return ret;
    }

    public String getString(String key, String def) {
        return config.getString(key, def);
    }

    public boolean isLocal() {
        return config.getString("environment", "").equals("local");
    }

    public static ApplicationConfig instance() {
        if (instance == null) {
            instance = new ApplicationConfig();
        }
        return instance;
    }
}
