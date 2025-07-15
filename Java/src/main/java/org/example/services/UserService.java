package org.example.services;

import jakarta.transaction.Transactional;
import org.example.dao.UserDao;
import org.example.models.User;
import org.example.repos.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }


    public Optional<User> findById(int id) {
        return userRepo.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepo.findAll();
    }

    @Transactional
    public void save(User user) {
        if (userRepo.existsByEmail(user.getEmail())) {
            String errorMessage = "Failed to save user. User with email + " + user.getEmail() + " already exists";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        logger.debug("User with email = {} saved successfully: ", user.getEmail());
        userRepo.save(user);
    }

    @Transactional
    public void update(User user) {
        if (!userRepo.existsByEmail(user.getEmail())) {
            String errorMessage = "Failed to update user. User with email + " + user.getEmail() + " does not exist";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        userRepo.save(user);
    }

    @Transactional
    public void delete(User user) {
        userRepo.delete(user);
    }
}
