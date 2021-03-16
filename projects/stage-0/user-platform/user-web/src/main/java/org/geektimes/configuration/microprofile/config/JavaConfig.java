package org.geektimes.configuration.microprofile.config;


import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.immutable.Triple;

import java.util.*;
import java.util.stream.Collectors;

public class JavaConfig implements Config {

    /**
     * 内部可变的集合，不要直接暴露在外面
     */
    private List<ConfigSource> configSources = new LinkedList<>();

    private static Comparator<ConfigSource> configSourceComparator = new Comparator<ConfigSource>() {
        @Override
        public int compare(ConfigSource o1, ConfigSource o2) {
            return Integer.compare(o2.getOrdinal(), o1.getOrdinal());
        }
    };

    public JavaConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceLoader<ConfigSource> serviceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
        serviceLoader.forEach(configSources::add);
        // 排序
        configSources.sort(configSourceComparator);
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        // String 转换成目标类型
        return null;
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        String propertyValue = getPropertyValue(propertyName);
        return new ConfigValue() {
            @Override
            public String getName() {
                return propertyName;
            }

            @Override
            public String getValue() {
                return null;
            }

            @Override
            public String getRawValue() {
                return null;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public int getSourceOrdinal() {
                return 0;
            }
        };
    }

    protected String getPropertyValue(String propertyName) {
        return getValueAndSource(propertyName).getMiddle();
    }

    @Override
    public <T> Optional<T> getOptionalValue(String propertyName, Class<T> propertyType) {
        T value = getValue(propertyName, propertyType);
        return Optional.ofNullable(value);
    }

    protected Triple<String, String, ConfigSource> getValueAndSource(String propertyName) {
        String propertyValue = null;
        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null) {
                return Triple.of(propertyName, propertyValue, configSource);
            }
        }
        return Triple.of();
    }

    @Override
    public Iterable<String> getPropertyNames() {
        List<String> collect = configSources.stream()
                .flatMap(configSource -> configSource.getPropertyNames().stream())
                .collect(Collectors.toList());
        return Collections.unmodifiableList(collect);
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableList(configSources);
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        return Optional.empty();
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        try {
            return type.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
