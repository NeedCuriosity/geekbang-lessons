package org.geektimes.oauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.Filter;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * 后装载WebSecurityConfigurerAdapter的filter优先
 *
 * @author zhouzy
 * @since 2021-04-21
 */
@Configuration
@Order
public class DefaultAuthConfig extends WebSecurityConfigurerAdapter {

    /**
     * todo filter/matcher/configurer 去重？ exceptionHandler如何合并？
     * todo 按match情况类型区分多个chain？能做到么
     *
     * @param webSecurity
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        HttpSecurity http = getHttp();
        LinkedHashMap<Class, List<SecurityConfigurer>> configurers = getField(http, "configurers");
        List<RequestMatcher> matchers = new LinkedList<>();
        List<Filter> filters = new LinkedList<>();

        List<SecurityBuilder<? extends SecurityFilterChain>> builders =
                getField(webSecurity, "securityFilterChainBuilders");
        Collections.reverse(builders);
        for (SecurityBuilder<? extends SecurityFilterChain> builder : builders) {
            if (builder instanceof HttpSecurity) {
                HttpSecurity httpSecurity = (HttpSecurity) builder;
                RequestMatcher requestMatcher = getField(httpSecurity, "requestMatcher");
                List<Filter> subFilters = getField(httpSecurity, "filters");
                LinkedHashMap<Class, List<SecurityConfigurer>> subConfigurers = getField(httpSecurity, "configurers");
                subConfigurers.forEach((klass, list) -> {
                    //todo 不同类型多个configurer处理逻辑不同？
                    if (!configurers.containsKey(klass)) {
                        configurers.put(klass, list);
                    }
                });
                filters.addAll(subFilters);
                matchers.add(requestMatcher);
                System.out.println(builder.toString());
            }
        }

        http.requestMatcher(new OrRequestMatcher(matchers));
        setField(http, "filters", filters);
        setField(webSecurity, "securityFilterChainBuilders", Collections.singletonList(http));
        super.configure(webSecurity);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(a -> a.antMatchers("/", "/error", "/webjars/**")
                .permitAll());
        http.csrf().and().cors();
    }

    private <V> V getField(Object o, String fieldName) throws IllegalAccessException {
        Class<?> clazz = o.getClass();
        Field field;
        for (; clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return (V) field.get(o);
            } catch (NoSuchFieldException ignored) {
                //ignored
            }
        }
        return null;
    }

    private void setField(Object o, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        field.set(o, value);
    }
}
