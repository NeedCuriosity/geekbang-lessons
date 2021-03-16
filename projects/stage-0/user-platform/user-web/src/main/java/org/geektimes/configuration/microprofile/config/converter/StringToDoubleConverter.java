package org.geektimes.configuration.microprofile.config.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author zhouzy
 * @since 2021-03-16
 */
public class StringToDoubleConverter implements Converter<Double> {
    @Override
    public Double convert(String value) throws IllegalArgumentException, NullPointerException {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Double.valueOf(value);
    }
}
