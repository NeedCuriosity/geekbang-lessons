package org.geektimes.configuration.microprofile.config.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author zhouzy
 * @since 2021-03-16
 */
public class StringToIntegerConverter implements Converter<Integer> {

    @Override
    public Integer convert(String s) throws IllegalArgumentException, NullPointerException {
        if (s == null || s.isEmpty()) return null;
        return Integer.valueOf(s);
    }
}
