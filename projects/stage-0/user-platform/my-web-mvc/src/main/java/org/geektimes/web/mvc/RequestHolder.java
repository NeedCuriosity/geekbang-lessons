package org.geektimes.web.mvc;

import org.eclipse.microprofile.config.Config;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhouzy
 * @since 2021-03-23
 */

public class RequestHolder {

    private static final ThreadLocal<Config> configHolder = new ThreadLocal<>();
    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void setConfig(Config config, HttpServletRequest request) {
        configHolder.set(config);
        requestHolder.set(request);
    }

    public static void reset() {
        configHolder.remove();
        requestHolder.remove();
    }

    public static <T> T getAttribute(String name) {
        Object result = requestHolder.get().getAttribute(name);
        if (result == null) {
            result = configHolder.get().getConfigValue(name).getValue();
        }
        return (T) result;
    }

    public static String getParameter(String name) {
        return requestHolder.get().getParameter(name);
    }
}
