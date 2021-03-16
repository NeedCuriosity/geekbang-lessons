package org.geektimes.container;

import org.geektimes.function.ThrowableAction;
import org.geektimes.ioc.Container;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @author zhouzy
 * @since 2021-03-15
 */
public class DefaultContainer implements Container {

    private static final String JNDI_ROOT = "java:comp/env";
    public static final String DEFAULT_CONTAINER = "defaultContainer";
    private static ServletContext servletContext;

    private ClassLoader classLoader;

    private Map<String, Object> newBeans = new HashMap<>();
    private Map<String, Object> singletonBeans = new HashMap<>();
    private List<String> jndiNames = new ArrayList<>();

    public void init(ServletContext servletContext) {
        try {
            DefaultContainer.servletContext = servletContext;
            this.classLoader = servletContext.getClassLoader();
            Context context = new InitialContext();
            loadJNDINames(context, JNDI_ROOT);
            loadJNDIBeans(context);
            servletContext.setAttribute(DEFAULT_CONTAINER, this);
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private void loadJNDIBeans(Context context) {
        try {
            for (String jndiName : jndiNames) {
                newBeans.put(jndiName.substring(JNDI_ROOT.length() + 1)
                        , context.lookup(jndiName));
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    private Object loadJNDIBean(String name) {
        try {
            Context context = new InitialContext();
            return context.lookup(name);
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadJNDINames(Context context, String name) {
        try {
            Object obj = context.lookup(name);
            if (obj instanceof Context) {
                NamingEnumeration<NameClassPair> pair = ((Context) obj).list("");
                while (pair.hasMoreElements()) {
                    NameClassPair next = pair.next();
                    loadJNDINames(context, name + "/" + next.getName());
                }
            } else {
                jndiNames.add(name);
            }
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Object getObject(String name) {
        Object obj = singletonBeans.get(name);
        if (obj == null) {
            obj = newBeans.get(name);
            if (obj == null) {
                obj = loadJNDIBean(name);
            }
            if (obj == null) {
                throw new RuntimeException("找不到相应的Bean:" + name);
            }
            injectResource(obj);
            initializeBean(obj);
            newBeans.remove(name);
            singletonBeans.put(name, obj);
        }
        return obj;
    }

    @Override
    public <C> C getComponent(String name) {
        return (C) getObject(name);
    }

    private void injectResource(Object obj) {
        Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers())
                        && field.isAnnotationPresent(Resource.class))
                .forEach(field -> {
                    Resource resource = field.getAnnotation(Resource.class);
                    String name = resource.name();
                    Object value = getObject(name);
                    field.setAccessible(true);
                    ThrowableAction.execute(() -> field.set(obj, value));
                });
    }

    private void initializeBean(Object obj) {
        Arrays.stream(obj.getClass().getMethods())
                .filter(method -> method.getParameterCount() == 0
                        && method.isAnnotationPresent(PostConstruct.class))
                .forEach(method -> ThrowableAction.execute(() -> method.invoke(obj)));
    }

    @Override
    public Container getParentContainer() {
        return null;
    }

    @Override
    public void setParentContainer(Container container) {

    }


    public ServletContext getServletContext() {
        return servletContext;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public static DefaultContainer getInstance() {
        return (DefaultContainer) servletContext.getAttribute(DEFAULT_CONTAINER);
    }
}
