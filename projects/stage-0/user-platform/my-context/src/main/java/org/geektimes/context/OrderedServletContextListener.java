package org.geektimes.context;

import javax.servlet.ServletContextListener;

/**
 * @author zhouzy
 * @since 2021-03-22
 */
public interface OrderedServletContextListener extends ServletContextListener {

    default int getOrder() {
        return 100;
    }
}
