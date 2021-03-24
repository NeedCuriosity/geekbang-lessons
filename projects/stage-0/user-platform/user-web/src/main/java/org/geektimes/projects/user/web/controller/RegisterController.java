package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.RequestHolder;
import org.geektimes.web.mvc.controller.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Objects;
import java.util.Set;

/**
 * @author zhouzy
 * @since 2021-03-02
 */
public class RegisterController implements RestController {

    @Resource(name = "bean/UserService")
    private UserService userService;

    @Path("/register")
    @POST
    @GET
    public String register(@Valid User user, HttpServletRequest request, HttpServletResponse response) throws Throwable {
        Set<String> errors = (Set<String>) request.getAttribute("error");
        if (errors != null && errors.size() > 0) {
            return "register.jsp";
        }
        if (userService.register(user)) {
            return "success.jsp";
        }
        return "fail.jsp";
    }

    @Path("/getValue")
    @GET
    public String getValue() {
        String value = Objects.isNull(RequestHolder.getParameter("value")) ?
                "application.name" : RequestHolder.getParameter("value");
        String attribute = RequestHolder.getAttribute(value);
        System.out.println(attribute);
        return attribute;
    }
}
