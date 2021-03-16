package org.geektimes.projects.user.web.listener;

import org.geektimes.container.DefaultContainer;
import org.geektimes.projects.user.context.ComponentContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class ContextInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("===========contextInitialized==============");
        try {
            DefaultContainer container = new DefaultContainer();
            container.init(sce.getServletContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComponentContext.getInstance().destroy();
        System.out.println("===========contextDestroyed==============");
    }
}
