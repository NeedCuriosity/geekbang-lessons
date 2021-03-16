package org.geektimes.configuration.microprofile.config.configvalue;

import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * @author zhouzy
 * @since 2021-03-17
 */
public class DefaultConfigValue implements ConfigValue {

    private final String name;
    private final String value;
    private final ConfigSource source;

    public DefaultConfigValue(String name, String value, ConfigSource source) {
        this.name = name;
        this.value = value;
        this.source = source;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getRawValue() {
        return value;
    }

    @Override
    public String getSourceName() {
        return source.getName();
    }

    @Override
    public int getSourceOrdinal() {
        return source.getOrdinal();
    }
}
