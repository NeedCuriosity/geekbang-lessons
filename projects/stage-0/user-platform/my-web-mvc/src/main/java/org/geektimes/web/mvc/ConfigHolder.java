package org.geektimes.web.mvc;

import org.eclipse.microprofile.config.Config;

/**
 * @author zhouzy
 * @since 2021-03-23
 */
public class ConfigHolder {

    private static final ThreadLocal<Config> holder = new ThreadLocal<>();

    public static void setConfig(Config config) {
        holder.set(config);
    }

    public static void reset() {
        holder.remove();
    }

    public static String getAttribute(String name) {
        return holder.get().getConfigValue(name).getValue();
    }
}
