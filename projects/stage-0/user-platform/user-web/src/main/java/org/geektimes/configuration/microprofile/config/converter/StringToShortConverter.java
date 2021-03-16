package org.geektimes.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author zhouzy
 * @since 2021-03-16
 */
public class StringToShortConverter implements Converter<Short> {
    @Override
    public Short convert(String value) throws IllegalArgumentException, NullPointerException {
        if (value.isEmpty()) {
            return null;
        }
        return Short.valueOf(value);
    }
}
