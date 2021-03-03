package org.geektimes.projects.user.web.listener;

import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.Connection;

@WebListener
public class DBConnectionInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("===========contextInitialized==============");
        try {
            Context context = new InitialContext();
            Context envContext = (Context) context.lookup("java:comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/UserPlatformDB");
            Connection connection = ds.getConnection();
            DBConnectionManager.getInstance().setConnection(connection);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("===========contextDestroyed==============");
        DBConnectionManager.getInstance().releaseConnection();
    }
}
