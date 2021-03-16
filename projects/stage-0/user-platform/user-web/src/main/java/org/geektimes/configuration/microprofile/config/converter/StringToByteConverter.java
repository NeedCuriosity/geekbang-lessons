package org.geektimes.configuration.microprofile.config.converter;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.spi.Converter;

/**
 * @author zhouzy
 * @since 2021-03-16
 */
public class StringToByteConverter implements Converter<Byte> {
    @Override
    public Byte convert(String value) throws IllegalArgumentException, NullPointerException {
        if (StringUtils.isBlank(value)) {
            return null;
        } else if (value.length() > 1) {
            throw new IllegalArgumentException();
        } else {
            return value.getBytes()[0];
        }
    }
}
