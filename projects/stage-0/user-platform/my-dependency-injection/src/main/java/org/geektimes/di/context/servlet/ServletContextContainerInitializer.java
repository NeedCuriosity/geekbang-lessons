package org.geektimes.di.context.servlet;

import org.geektimes.context.OrderedComponentInitializer;
import org.geektimes.di.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletContextContainerInitializer implements OrderedComponentInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ComponentContext componentContext = new ComponentContext();
        componentContext.init(servletContext);
    }

    @Override
    public int getOrder() {
        return 200;
    }
}
