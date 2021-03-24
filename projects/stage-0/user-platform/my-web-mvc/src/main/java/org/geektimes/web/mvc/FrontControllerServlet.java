package org.geektimes.web.mvc;

import org.apache.commons.lang.StringUtils;
import org.eclipse.microprofile.config.ConfigProvider;
import org.geektimes.context.util.ClassUtils;
import org.geektimes.di.context.ComponentContext;
import org.geektimes.web.mvc.controller.Controller;
import org.geektimes.web.mvc.controller.PageController;
import org.geektimes.web.mvc.controller.RestController;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.substringAfter;

public class FrontControllerServlet extends HttpServlet {

    /**
     * 请求路径和 Controller 的映射关系缓存
     */
    private Map<String, Controller> controllersMapping = new HashMap<>();

    /**
     * 请求路径和 {@link HandlerMethodInfo} 映射关系缓存
     */
    private Map<String, HandlerMethodInfo> handleMethodInfoMapping = new HashMap<>();

    private ComponentContext componentContext = ComponentContext.getInstance();

    /**
     * 初始化 Servlet
     *
     * @param servletConfig
     */
    public void init(ServletConfig servletConfig) {
        initHandleMethods();
    }

    /**
     * 读取所有的 RestController 的注解元信息 @Path
     * 利用 ServiceLoader 技术（Java SPI）
     */
    private void initHandleMethods() {
        ComponentContext componentContext = ComponentContext.getInstance();
        for (Controller controller : ServiceLoader.load(Controller.class, ClassUtils.getClassLoader())) {
            Class<?> controllerClass = controller.getClass();
            Arrays.stream(controllerClass.getDeclaredFields())
                    .filter(field -> !Modifier.isStatic(field.getModifiers())
                            && field.isAnnotationPresent(Resource.class))
                    .forEach(field -> {
                        field.setAccessible(true);
                        Resource resource = field.getAnnotation(Resource.class);
                        try {
                            field.set(controller, componentContext.getComponent(resource.name()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
            Path pathFromClass = controllerClass.getAnnotation(Path.class);
            Method[] publicMethods = controllerClass.getMethods();
            // 处理方法支持的 HTTP 方法集合
            String requestPath = "";
            String classRequestPath = "";
            if (pathFromClass != null) {
                classRequestPath = pathFromClass.value();
                controllersMapping.put(classRequestPath, controller);
            }
            for (Method method : publicMethods) {
                Set<String> supportedHttpMethods = findSupportedHttpMethods(method);
                Path pathFromMethod = method.getAnnotation(Path.class);
                if (pathFromMethod != null) {
                    requestPath = classRequestPath + pathFromMethod.value();
                    handleMethodInfoMapping.put(requestPath,
                            new HandlerMethodInfo(requestPath, method, supportedHttpMethods));
                    controllersMapping.put(requestPath, controller);
                }
            }
        }
    }

    /**
     * 获取处理方法中标注的 HTTP方法集合
     *
     * @param method 处理方法
     * @return
     */
    private Set<String> findSupportedHttpMethods(Method method) {
        Set<String> supportedHttpMethods = new LinkedHashSet<>();
        for (Annotation annotationFromMethod : method.getAnnotations()) {
            HttpMethod httpMethod = annotationFromMethod.annotationType().getAnnotation(HttpMethod.class);
            if (httpMethod != null) {
                supportedHttpMethods.add(httpMethod.value());
            }
        }

        if (supportedHttpMethods.isEmpty()) {
            supportedHttpMethods.addAll(asList(HttpMethod.GET, HttpMethod.POST,
                    HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.HEAD, HttpMethod.OPTIONS));
        }

        return supportedHttpMethods;
    }

    /**
     * SCWCD
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 建立映射关系
        // requestURI = /a/hello/world
        String requestURI = request.getRequestURI();
        // contextPath  = /a or "/" or ""
        String servletContextPath = request.getContextPath();
        String prefixPath = servletContextPath;
        // 映射路径（子路径）
        String requestMappingPath = substringAfter(requestURI,
                StringUtils.replace(prefixPath, "//", "/"));
        // 映射到 Controller
        Controller controller = controllersMapping.get(requestMappingPath);

        if (controller != null) {

            HandlerMethodInfo handlerMethodInfo = handleMethodInfoMapping.get(requestMappingPath);

            try {
                if (handlerMethodInfo != null) {

                    String httpMethod = request.getMethod();

                    if (!handlerMethodInfo.getSupportedHttpMethods().contains(httpMethod)) {
                        // HTTP 方法不支持
                        response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                        return;
                    }

                    RequestHolder.setConfig(ConfigProvider.getConfig(), request);

                    if (controller instanceof PageController) {
                        PageController pageController = PageController.class.cast(controller);
                        String viewPath = pageController.execute(request, response);
                        // 页面请求 forward
                        // request -> RequestDispatcher forward
                        // RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
                        // ServletContext -> RequestDispatcher forward
                        // ServletContext -> RequestDispatcher 必须以 "/" 开头
                        ServletContext servletContext = request.getServletContext();
                        if (!viewPath.startsWith("/")) {
                            viewPath = "/" + viewPath;
                        }
                        RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(viewPath);
                        requestDispatcher.forward(request, response);
                        return;
                    } else if (controller instanceof RestController) {
                        restControllerMethodHandle(request, response, controller, handlerMethodInfo);
                        return;
                    }

                }
            } catch (Throwable throwable) {
                if (throwable.getCause() instanceof IOException) {
                    throw (IOException) throwable.getCause();
                } else {
                    throw new ServletException(throwable.getCause());
                }
            } finally {
                RequestHolder.reset();
            }
        }
    }

    private void restControllerMethodHandle(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Controller controller,
                                            HandlerMethodInfo handlerMethodInfo) throws IOException, InvocationTargetException, IllegalAccessException, ServletException {
        Class<?>[] parameterTypes = handlerMethodInfo.getHandlerMethod().getParameterTypes();
        Object[] parameters = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            if (parameterType.equals(HttpServletRequest.class)) {
                parameters[i] = request;
            } else if (parameterType.equals(HttpServletResponse.class)) {
                parameters[i] = response;
            } else {
                parameters[i] = mapping(request.getParameterMap(), parameterType);
            }
        }
        Set<String> errors = validateParameter(handlerMethodInfo.getHandlerMethod(), parameters);
        if (errors.size() > 0) {
            request.setAttribute("error", errors);
        }

        Object result = handlerMethodInfo.getHandlerMethod().invoke(controller, parameters);
        if (result instanceof String) {
            String str = String.class.cast(result);
            if (str.endsWith(".jsp") || str.endsWith(".html")) {
                request.getRequestDispatcher(str).forward(request, response);
                return;
            }
        }
        //todo
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.getWriter().write(Objects.isNull(result) ? "" : result.toString());
        response.flushBuffer();
    }

    private Set<String> validateParameter(Method handlerMethod, Object[] parameters) {
        Annotation[][] parameterAnnotations = handlerMethod.getParameterAnnotations();
        Set<String> validates = new LinkedHashSet<>();
        for (int i = 0; i < parameters.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            boolean needValidation = Arrays.stream(annotations)
                    .anyMatch(annotation -> Valid.class.isAssignableFrom(annotation.getClass()));

            if (needValidation) {
                Validator validator = componentContext.getComponent("bean/Validator");
                Set<ConstraintViolation<Object>> validate = validator.validate(parameters[i]);

                validate.forEach(c -> validates.add(c.getPropertyPath().toString() + " " + c.getMessage()));
            }
        }
        return validates;
    }

    private Object mapping(Map<String, String[]> parameterMap, Class<?> parameterType) {
        if (parameterType.isArray()
                || Collection.class.isAssignableFrom(parameterType)
                || Map.class.isAssignableFrom(parameterType))

            //todo anyway..
            return null;
        try {
            Object pojo = parameterType.getConstructor().newInstance();

            BeanInfo beanInfo = Introspector.getBeanInfo(parameterType, Object.class);
            for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                String fieldName = propertyDescriptor.getName();
                String[] values = parameterMap.get(fieldName);
                if (values != null && values.length > 0) {
                    //todo 只处理一个
                    String fieldValue = values[0];
                    Method writeMethod = propertyDescriptor.getWriteMethod();
                    writeMethod.invoke(pojo, fieldValue);
                }
            }

            return pojo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    private void beforeInvoke(Method handleMethod, HttpServletRequest request, HttpServletResponse response) {
//
//        CacheControl cacheControl = handleMethod.getAnnotation(CacheControl.class);
//
//        Map<String, List<String>> headers = new LinkedHashMap<>();
//
//        if (cacheControl != null) {
//            CacheControlHeaderWriter writer = new CacheControlHeaderWriter();
//            writer.write(hseaders, cacheControl.value());
//        }
//    }
}
