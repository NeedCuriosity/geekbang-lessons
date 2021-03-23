package org.geektimes.di.context.servlet;

import org.geektimes.context.OrderedServletContextListener;
import org.geektimes.di.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.annotation.WebListener;


@WebListener
public class ServletContextContainerInitializer implements OrderedServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        ComponentContext componentContext = new ComponentContext();
        componentContext.init(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    @Override
    public int getOrder() {
        return 200;
    }
}
