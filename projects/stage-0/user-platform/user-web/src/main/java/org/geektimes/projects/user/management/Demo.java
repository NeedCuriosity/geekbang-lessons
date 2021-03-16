package org.geektimes.projects.user.management;

import org.geektimes.projects.user.domain.User;

import javax.management.*;
import javax.management.modelmbean.RequiredModelMBean;
import java.lang.management.ManagementFactory;

/**
 * @author zhouzy
 * @since 2021-03-16
 */
public class Demo {

    public static void main(String[] args) throws MalformedObjectNameException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, InterruptedException {

        User u = new User();
        u.setPhoneNumber("3344");
        u.setName("hehe");
        RequiredModelMBean mBean = ModelMBeanFactory.createModelMBean(u, "ObjectReference");
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName("org.geektimes.projects.user.domain:type=ObjectReference");
        mBeanServer.registerMBean(mBean, objectName);
        while (true) {
            System.out.println(u);
            Thread.sleep(2000L);
        }
    }
}
