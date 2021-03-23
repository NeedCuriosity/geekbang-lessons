package org.geektimes.context.util;

/**
 * @author zhouzy
 * @since 2021-03-23
 */
public class ClassUtils {

    private static ClassLoader classLoader;

    public static ClassLoader getClassLoader() {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        return classLoader;
    }
}
