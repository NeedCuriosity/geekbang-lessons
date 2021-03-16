package org.geektimes.configuration.microprofile.config;


import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.immutable.Triple;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.util.*;
import java.util.stream.Collectors;

public class JavaConfig implements Config {

    /**
     * 内部可变的集合，不要直接暴露在外面
     */
    private List<ConfigSource> configSources = new LinkedList<>();
    private Map<Class, Converter> converterMap = new HashMap<>();

    private static Comparator<ConfigSource> configSourceComparator = Comparator.comparingInt(ConfigSource::getOrdinal);

    public JavaConfig() {
        ClassLoader classLoader = getClass().getClassLoader();
        ServiceLoader<ConfigSource> serviceLoader = ServiceLoader.load(ConfigSource.class, classLoader);
        serviceLoader.forEach(configSources::add);
        // 排序
        configSources.sort(configSourceComparator);

        ServiceLoader<Converter> converterServiceLoader = ServiceLoader.load(Converter.class);
        Iterator<Converter> iterator = converterServiceLoader.iterator();
        while (iterator.hasNext()) {
            Converter converter = iterator.next();
            Class aClass =
                    (Class) ((ParameterizedTypeImpl) converter.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
            converterMap.put(aClass, converter);
        }
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        Converter<T> converter = converterMap.get(propertyType);
        if (converter != null) {
            return converter.convert(propertyValue);
        }
        // String 转换成目标类型
        throw new RuntimeException("converter not found for type:"
                + propertyType.getName());
    }

    @Override
    public ConfigValue getConfigValue(String propertyName) {
        Triple<String, String, ConfigSource> valueAndSource =
                getValueAndSource(propertyName);
        return new ConfigValue() {
            @Override
            public String getName() {
                return valueAndSource.getLeft();
            }

            @Override
            public String getValue() {
                return valueAndSource.getMiddle();
            }

            @Override
            public String getRawValue() {
                return valueAndSource.getLeft();
            }

            @Override
            public String getSourceName() {
                return valueAndSource.getRight().getName();
            }

            @Override
            public int getSourceOrdinal() {
                return valueAndSource.getRight().getOrdinal();
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
