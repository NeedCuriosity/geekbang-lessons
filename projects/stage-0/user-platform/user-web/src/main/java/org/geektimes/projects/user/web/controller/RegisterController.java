package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.context.ComponentContext;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Optional;
import java.util.Set;

/**
 * @author zhouzy
 * @since 2021-03-02
 */
@Path("/register")
public class RegisterController implements PageController {

    @Resource(name = "bean/UserService")
    private UserService userService;

    @Resource(name = "bean/Validator")
    private Validator validator;

    @POST
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String name = request.getParameter("userName");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        String phoneNumber = request.getParameter("phoneNumber");
        if (name == null) {
            return "register.jsp";
        }
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);

        Set<ConstraintViolation<User>> validates = validator.validate(user);
        Optional<String> message = validates.stream().map(c ->
                c.getPropertyPath().toString() + " " + c.getMessage()).findAny();
        if (message.isPresent()) {
            System.out.println("===========" + message.get() + "===========");
            return "register.jsp";
        } else if (userService.register(user)) {
            return "success.jsp";
        }
        return "fail.jsp";
    }
}
