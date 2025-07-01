package org.example.dao;

import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;
import org.example.models.User;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserDao {

    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public User findById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.byId(User.class).load(id);
        } catch (Exception e) {
            logger.error("Failed to find user by id = {}", id, e);
            throw new RuntimeException("Failed to find user by id = " + id, e);
        }
    }

    public User findByEmail(String email) {
        if (email == null) {
            logger.error("Failed to find user by email. Email cannot be empty.");
            throw new IllegalArgumentException("Failed to find user by email. Email cannot be empty.");
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // В БД создана функция для поиска по email:
            return (User) session.createNativeQuery("SELECT * FROM find_user_by_email (:input_email)", User.class)
                    .setParameter("input_email",email)
                    .getSingleResult();
        }
        // Если выбросится исключение о несуществующем пользователе
        catch (NoResultException nre) {
            logger.error("User with email = {} does not exist", email, nre);
            throw new RuntimeException("User with email = " + email + " does not exist", nre);
        }
        catch (Exception e) {
            logger.error("Failed to find user by email = {}", email, e);
            throw new RuntimeException("Failed to find user by email = " + email, e);
        }
    }

    public List<User> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();
        } catch (Exception e) {
            logger.error("Failed to find all users", e);
            throw new RuntimeException("Failed to find all users", e);
        }
    }

    public void save(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(user);
                transaction.commit();
                logger.debug("User with id = {} saved successfully: ", user.getId());
            }
            // Исключение в случае сохранения user с существующим email
            catch (ConstraintViolationException cve) {
                logger.error("User with email = {} already exists", user.getEmail(), cve);
                throw new RuntimeException("User with email = " + user.getEmail() + " already exists", cve);
            }
            catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error("Failed to save user with id = {}", user.getId(), e);
                throw new RuntimeException("Failed to save user with name = " + user.getName() + " and email = " + user.getEmail(), e);
            }
        }
    }

    public void update(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // Если пользователя нет в БД
                if (session.find(User.class, user.getId()) == null) {
                    logger.error("Failed to update user with email = {} ", user.getEmail() + ". User does not exist");
                    throw new EntityNotFoundException("Failed to update user with email = " + user.getEmail() + ". User does not exist");
                }
                session.merge(user);
                transaction.commit();
                logger.debug("User with id = {} updated successfully: ", user.getId());
            }
            catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error("Failed to update user with id = {}", user.getId(), e);
                throw new RuntimeException("Failed to update user with name = " + user.getName() + " and email = " + user.getEmail(), e);
            }
        }
    }

    public void delete(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.remove(user);
                transaction.commit();
                logger.debug("User with id = {} deleted successfully: ", user.getId());
            } catch (Exception e) {
                if (transaction.isActive()) {
                    transaction.rollback();
                }
                logger.error("Failed to delete user with id = {}", user.getId(), e);
                throw new RuntimeException("Failed to delete user with name = " + user.getName() + " and email = " + user.getEmail(), e);
            }
        }
    }
}