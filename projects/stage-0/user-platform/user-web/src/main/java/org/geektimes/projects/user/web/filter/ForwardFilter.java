package org.geektimes.projects.user.web.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ForwardFilter
 */
public class ForwardFilter implements Filter {

    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("ForwardFilter init");
        servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            servletContext.log("ForwardFilter 被触发了");
            // CharsetEncodingFilter -> FrontControllerServlet -> forward -> index.jsp
        }

        // 执行过滤链
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
