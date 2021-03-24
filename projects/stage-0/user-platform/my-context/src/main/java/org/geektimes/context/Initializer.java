package org.geektimes.context;

import org.geektimes.context.util.ClassUtils;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
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
@HandlesTypes(OrderedComponentInitializer.class)
public class Initializer implements ServletContainerInitializer {
    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        ClassUtils.getClassLoader();
        List<OrderedComponentInitializer> initializers = new LinkedList<>();
        Comparator<OrderedComponentInitializer> comparator = Comparator.comparingInt(OrderedComponentInitializer::getOrder);
        try {
            if (c != null) {
                for (Class<?> initializer : c) {
                    if (!initializer.isInterface()
                            && !Modifier.isAbstract(initializer.getModifiers())
                            && OrderedComponentInitializer.class.isAssignableFrom(initializer)) {
                        initializers.add((OrderedComponentInitializer) initializer.getConstructor().newInstance());
                    }
                }
                initializers.sort(comparator);
                for (OrderedComponentInitializer initializer : initializers) {
                    initializer.onStartup(ctx);
                }
            }
        } catch (Throwable e) {
            throw new ServletException(e);
        }
    }
}
