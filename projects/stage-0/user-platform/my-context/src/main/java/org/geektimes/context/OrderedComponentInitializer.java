package org.geektimes.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @author zhouzy
 * @since 2021-03-24
 */
public interface OrderedComponentInitializer {

    void onStartup(ServletContext servletContext) throws ServletException;

    default int getOrder() {
        return 100;
    }
}
