package org.example.controllers;

import org.example.dao.UserDao;
import org.example.models.User;
import org.example.services.UserService;

import java.util.List;

public class UserController {

    private final UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public User findById(int id) {
        return userService.findById(id);
    }

    public User findByEmail(String email) {
        return userService.findByEmail(email);
    }

    public List<User> findAll() {
        return userService.findAll();
    }

    public void save(User user) {
        userService.save(user);
    }

    public void update(User user) {
        userService.update(user);
    }

    public void delete(User user) {
        userService.delete(user);
    }
}
