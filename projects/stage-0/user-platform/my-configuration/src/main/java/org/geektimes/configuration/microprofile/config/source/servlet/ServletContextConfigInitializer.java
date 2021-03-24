package org.geektimes.configuration.microprofile.config.source.servlet;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.context.OrderedComponentInitializer;
import org.geektimes.context.util.ClassUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class ServletContextConfigInitializer implements OrderedComponentInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ServletContextConfigSource servletContextConfigSource = new ServletContextConfigSource(servletContext);
        // 获取当前 ClassLoader
        ClassLoader classLoader = ClassUtils.getClassLoader();
        ConfigProviderResolver configProviderResolver = ConfigProviderResolver.instance();
        ConfigBuilder configBuilder = configProviderResolver.getBuilder();
        // 配置 ClassLoader
        configBuilder.forClassLoader(classLoader);
        // 默认配置源（内建的，静态的）
        configBuilder.addDefaultSources();
        // 通过发现配置源（动态的）
        configBuilder.addDiscoveredConverters();
        // 增加扩展配置源（基于 Servlet 引擎）
        configBuilder.withSources(servletContextConfigSource);
        // 获取 Config
        Config config = configBuilder.build();
        // 注册 Config 关联到当前 ClassLoader
        configProviderResolver.registerConfig(config, classLoader);
    }

    @Override
    public int getOrder() {
        return 300;
    }
}
