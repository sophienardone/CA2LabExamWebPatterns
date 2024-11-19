package webpatterns.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import webpatterns.model.User;
import webpatterns.persistence.UserDao;
import webpatterns.persistence.UserDaoImpl;

@Slf4j
@Controller
public class UserController {

    @PostMapping("registerUser")
    public String registerUser(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "isAdmin") boolean isAdmin,
            Model model, HttpSession session) {
        String view = "";
        UserDao userDao = new UserDaoImpl("database.properties");
        User u = new User(username, password, firstName, lastName, isAdmin);
        boolean isAdded = userDao.addUser(u);
        if (isAdded) {
            view = "registerSuccess";
            model.addAttribute("registeredUser", u);
            log.info("User {} registered", u.getUsername());
        } else {
            view = "registerFailed";
            log.info("Registration failed while handling username {}", username);
        }
        return view;
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam(name = "username") String username,
            @RequestParam(name = "password") String password,
            Model model, HttpSession session) {

        if (username.isBlank() || password.isBlank()) {
            return "error";
        }

        UserDao userDao = new UserDaoImpl("database.properties");
        User u = userDao.login(username, password);

        if (u == null) {
            String message = "No such username/password";
            model.addAttribute("message", message);
            return "loginFailed";
        }

        session.setAttribute("loggedInUser", u);
        return "loginSuccessful";

    }


}
