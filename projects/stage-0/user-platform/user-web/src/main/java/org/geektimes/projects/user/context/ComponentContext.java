package org.geektimes.projects.user.context;

import org.geektimes.function.ThrowableAction;
import org.geektimes.function.ThrowableFunction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhouzy
 * @since 2021-03-08
 */
public class ComponentContext {

    public static final String CONTEXT_ENV_NAME = "java:comp/env";
    public static final String CONTEXT_NAME = ComponentContext.class.getName();


    private static ServletContext servletContext;
    private ClassLoader classLoader;
    private Context envContext;
    //缓存
    private Map<String, Object> componentMap = new LinkedHashMap<>();
    private Map<Object, List<Method>> preDestroyMap = new LinkedHashMap<>();


    public static ComponentContext getInstance() {
        return (ComponentContext) servletContext.getAttribute(CONTEXT_NAME);
    }

    public void init(ServletContext servletContext) {
        servletContext.setAttribute(CONTEXT_NAME, this);
        ComponentContext.servletContext = servletContext;
        this.classLoader = servletContext.getClassLoader();
        intEnvContext();
        instantiateComponents();
        initializeComponents();
    }


    private void initializeComponents() {
        componentMap.values().forEach(component -> {
            Class<?> componentClass = component.getClass();
            injectComponent(component, componentClass);
            processPostConstruct(component, componentClass);
            List<Method> method = findPreDestroyMethod(component, componentClass);
            if (method.size() > 0) {
                preDestroyMap.put(component, method);
            }
        });
    }

    private List<Method> findPreDestroyMethod(Object component, Class<?> componentClass) {
        return Stream.of(componentClass.getMethods())
                .filter(method -> method.getParameterCount() == 0
                        && method.isAnnotationPresent(PreDestroy.class))
                .collect(Collectors.toList());
    }

    private void processPostConstruct(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getMethods())
                .filter(method ->
                        method.getParameterCount() == 0
                                && method.isAnnotationPresent(PostConstruct.class))
                .forEach(method -> {
                    try {
                        method.invoke(component);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void injectComponent(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getDeclaredFields())
                .filter(field -> {
                    int modifiers = field.getModifiers();
                    return !Modifier.isStatic(modifiers) //非static
                            && field.isAnnotationPresent(Resource.class); //有resourceAnnotation

                }).forEach(field -> {
            Resource resource = field.getAnnotation(Resource.class);
            String name = resource.name();
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            try {
                field.set(component, componentMap.get(name));
            } catch (IllegalAccessException e) {
                //ignore
            }
        });
    }

    protected void intEnvContext() {
        if (envContext != null) {
            return;
        }
        Context context = null;
        try {
            context = new InitialContext();
            this.envContext = (Context) context.lookup(CONTEXT_ENV_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } finally {
            close(context);
        }
    }

    protected void instantiateComponents() {
        List<String> componentNames = listAllComponentNames();
        componentNames.forEach(name -> componentMap.put(name, lookupComponent(name)));
    }


    private void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(context::close);
        }
    }

    private List<String> listAllComponentNames() {
        return listComponentNames("/");
    }

    private <C> C lookupComponent(String name) {
        return executeInContext(context -> (C) context.lookup(name));
    }

    private void processPreDestory() {
        preDestroyMap.forEach((component, methods) -> {
            for (Method method : methods) {
                try {
                    ThrowableAction.execute(() -> method.invoke(component));
                } catch (Throwable e) {
                    System.out.println(component.getClass().getName()
                            + " " + method.getName() + "preDestroy执行失败");
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public <C> C getComponent(String name) {
        return (C) componentMap.get(name);
    }

    public void destroy() throws RuntimeException {
        processPreDestory();
        try {
            close(this.envContext);
        } catch (Throwable e) {
            if (!e.getMessage().contains("read only")) {
                throw e;
            }
        }
    }

    protected List<String> listComponentNames(String name) {
        if (name == null) {
            return Collections.emptyList();
        }

        return executeInContext(context -> {
            NamingEnumeration<NameClassPair> elements =
                    executeInContext(context, ctx -> ctx.list(name), true);
            if (elements == null) {
                return Collections.emptyList();
            }
            List<String> fullNames = new LinkedList<>();
            while (elements.hasMoreElements()) {
                NameClassPair element = elements.next();
                String className = element.getClassName();
                Class<?> targetClass = classLoader.loadClass(className);
                if (Context.class.isAssignableFrom(targetClass)) {
                    // 如果当前名称是目录（Context 实现类）的话，递归查找
                    fullNames.addAll(listComponentNames(element.getName()));
                } else {
                    // 否则，当前名称绑定目标类型的话话，添加该名称到集合中
                    String fullName = name.startsWith("/") ? element.getName() :
                            name + "/" + element.getName();
                    ;
                    fullNames.add(fullName);
                }
            }
            return fullNames;
        });
    }

    protected <R> R executeInContext(ThrowableFunction<Context, R> function) {
        return executeInContext(function, false);
    }


    protected <R> R executeInContext(ThrowableFunction<Context, R> function, boolean ignoreException) {
        return executeInContext(this.envContext, function, ignoreException);
    }

    protected <R> R executeInContext(Context context,
                                     ThrowableFunction<Context, R> function,
                                     boolean ignoreException) {
        R result = null;
        try {
            result = function.apply(context);
        } catch (Throwable throwable) {
            if (ignoreException) {
                System.out.println("exception ignored");
            } else {
                throw new RuntimeException(throwable);
            }
        }
        return result;
    }
}
