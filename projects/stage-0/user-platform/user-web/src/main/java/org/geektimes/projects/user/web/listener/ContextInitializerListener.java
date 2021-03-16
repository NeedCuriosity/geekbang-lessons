package org.geektimes.projects.user.web.listener;

import org.geektimes.container.DefaultContainer;
import org.geektimes.projects.user.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.management.ModelMBeanFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.modelmbean.RequiredModelMBean;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;

public class ContextInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("===========contextInitialized==============");
        try {
            DefaultContainer container = new DefaultContainer();
            container.init(sce.getServletContext());
            User u = new User();
            RequiredModelMBean mBean = ModelMBeanFactory.createModelMBean(u, "ObjectReference");
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName("org.geektimes.projects.user.domain:type=ObjectReference");
            mBeanServer.registerMBean(mBean, objectName);
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
