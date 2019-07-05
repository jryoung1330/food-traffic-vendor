package com.lococator.controller;

import com.lococator.entity.User;
import com.lococator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @GetMapping("/checkUsernameAvailability/{username}")
    public boolean checkForUser(@PathVariable String username){
        return userService.isUsernameClaimed(username);
    }

    @PostMapping("/login")
    public User loginUser(@RequestBody User userToAuth, HttpServletResponse response, @CookieValue(value = "UBID", defaultValue="UBID") String token) {
        String password = userToAuth.getPasswordHash();
        String username = userToAuth.getUsername();
        User user = userService.loginUser(username, password);
        Cookie cookie = new Cookie("UID", user.getPasswordHash());
        cookie.setPath("/");
        response.addCookie(cookie);
        user.setPasswordHash(null);
        return user;
    }

    @PostMapping("")
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/{id}")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}
