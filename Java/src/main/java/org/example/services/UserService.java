package org.example.services;

import org.example.dao.UserDao;
import org.example.models.User;

import java.util.List;

public class UserService {

    private final UserDao userDao;

    public UserService () {
        this.userDao = new UserDao();
    }

    public User findById (int id)  {
        return userDao.findById(id);
    }

    public User findByEmail (String email)  {
        return userDao.findByEmail(email);
    }

    public List<User> findAll()  {
        return userDao.findAll();
    }

    public void save(User user)  {
        userDao.save(user);
    }

    public void update(User user) {
        userDao.update(user);
    }

    public void delete(User user) {
        userDao.delete(user);
    }
}
