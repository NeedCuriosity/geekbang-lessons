package org.geektimes.projects.user.web.listener;

import org.geektimes.context.OrderedServletContextListener;
import org.geektimes.di.context.ComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

public class ComponentContextInitializerListener implements OrderedServletContextListener {

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ComponentContext context = new ComponentContext();
        context.init(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
//        ComponentContext context = ComponentContext.getInstance();
//        context.destroy();
    }

    @Override
    public int getOrder() {
        return 99;
    }
}