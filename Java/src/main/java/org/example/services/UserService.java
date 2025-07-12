package org.example.services;

import org.example.dao.UserDao;
import org.example.models.User;

import java.util.List;

public class UserService {

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDao();
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }


    public User findById(int id) {
        try {
            return userDao.findById(id);
        }
        // В методах, возвращающих значения, исключения перехватываются в сервисе
        // и возвращают null
        catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }

    }

    public User findByEmail(String email) {
        try {
            return userDao.findByEmail(email);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public List<User> findAll() {
        try {
            return userDao.findAll();
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
            return null;
        }
    }

    public void save(User user) {
        userDao.save(user);
    }

    public void update(User user) {
        userDao.update(user);
    }

    public void delete(User user) {
        try {
            userDao.delete(user);
        } catch (RuntimeException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
