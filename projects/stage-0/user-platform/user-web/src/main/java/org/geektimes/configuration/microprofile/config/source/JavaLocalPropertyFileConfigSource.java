package org.geektimes.configuration.microprofile.config.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author zhouzy
 * @since 2021-03-16
 */
public class JavaLocalPropertyFileConfigSource implements ConfigSource {

    private final Map<String, String> properties;

    public JavaLocalPropertyFileConfigSource() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream resourceAsStream = classLoader.getResourceAsStream("META-INF/config.properties");
        Properties props = new Properties();
        try {
            props.load(resourceAsStream);
            properties = props.stringPropertyNames().stream()
                    .collect(Collectors.toMap(Function.identity(),
                            props::getProperty, (v1, v2) -> v2));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return "LocalFileConfigSource";
    }
}
