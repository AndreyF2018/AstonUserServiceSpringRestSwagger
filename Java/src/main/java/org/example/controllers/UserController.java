package org.example.controllers;

import org.example.models.User;
import org.example.services.UserService;

import java.util.List;

public class UserController {

    private final UserService userService;

    public UserController () {
        this.userService = new UserService();
    }

    public User finById (int id)  {
        try {
            return userService.findById(id);
        }
        // В методах, возвращающих значения, исключения перехватывают контроллеры
        // и возвращают null
        catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }

    }

    public User findByEmail(String email)  {
        try {
            return userService.findByEmail(email);
        }
        catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public List<User> findAll()  {
        try{
            return userService.findAll();
        }
        catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public void save(User user)  {
            userService.save(user);
    }

    public void update(User user) {
        userService.update(user);
    }

    public void delete(User user) {
        try {
            userService.delete(user);
        }
        catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
