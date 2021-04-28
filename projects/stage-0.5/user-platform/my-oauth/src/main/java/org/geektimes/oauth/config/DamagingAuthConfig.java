package org.geektimes.oauth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author zhouzy
 * @since 2021-04-21
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DamagingAuthConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //do some damage
        http.authorizeRequests().anyRequest().permitAll();
    }
}
