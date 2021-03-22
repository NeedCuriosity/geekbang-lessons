package org.geektimes.context;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author zhouzy
 * @since 2021-03-22
 */
@HandlesTypes(OrderedServletContextListener.class)
public class Initializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        List<OrderedServletContextListener> listeners = new LinkedList<>();
        Comparator<OrderedServletContextListener> comparator = Comparator.comparingInt(OrderedServletContextListener::getOrder);
        try {
            if (c != null) {
                for (Class<?> listener : c) {
                    if (!listener.isInterface()
                            && !Modifier.isAbstract(listener.getModifiers())
                            && ServletContextListener.class.isAssignableFrom(listener)) {
                        listeners.add((OrderedServletContextListener) listener.getConstructor().newInstance());
                    }
                }
                listeners.sort(comparator);
                listeners.forEach(ctx::addListener);
            }
        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }
}
