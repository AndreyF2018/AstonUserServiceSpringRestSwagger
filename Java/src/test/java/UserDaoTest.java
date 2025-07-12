import jakarta.persistence.EntityNotFoundException;
import org.example.dao.UserDao;
import org.example.models.User;
import org.example.utils.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

@Testcontainers
public class UserDaoTest {

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17.5-alpine")
            .withInitScript("UsersDB.sql");

    private static UserDao userDao;
    private Session session;

    @BeforeAll
    static void setup() {
        userDao = new UserDao();
        System.setProperty("test.db.url", postgres.getJdbcUrl());
        System.setProperty("test.db.username", postgres.getUsername());
        System.setProperty("test.db.password", postgres.getPassword());
        System.setProperty("hibernate.config.file", "testHibernate.cfg.xml");
        postgres.start();
    }

    @BeforeEach
    void clearUserTable () {
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @AfterAll
    static void  terDown(){
        postgres.stop();
    }

    @AfterEach
    void rollbackTransaction() {
        if (session != null) {
            if(session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            session.close();
        }
    }

    @Test
    void findById_validId_returnUser(){
        User expectedUser = new User ("Test", "test@example.com", 20, LocalDateTime.now());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(expectedUser);
            transaction.commit();
        }
        User actualUser = userDao.findById(expectedUser.getId());
        assertEquals(expectedUser.getId(), actualUser.getId());
    }

    @Test
    void findById_invalidId_returnNull(){
        assertNull(userDao.findById(-1));
    }

    @Test
    void findByEmail_validEmail_returnUser(){
        User expectedUser = new User ("Test", "test@example.com", 20, LocalDateTime.now());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(expectedUser);
            transaction.commit();
        }
        User actualUser = userDao.findByEmail(expectedUser.getEmail());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    void findByEmail_InvalidEmail_ThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> userDao.findByEmail(" "));
    }

    @Test
    void findAll_notEmptyDB_returnAllUsers () {
        User user1 = new User ("Test1", "test1@example.com", 20, LocalDateTime.now());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user1);
            transaction.commit();
        }
        User user2 = new User ("Test2", "test2@example.com", 21, LocalDateTime.now());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(user2);
            transaction.commit();
        }
        assertEquals(2, userDao.findAll().size());
    }

    @Test
    void save_validUser_persistUser() {
        User newUser = new User("Test", "test@example.com", 20, LocalDateTime.now());
        userDao.save(newUser);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User savedUser = session.find(User.class, newUser.getId());
            assertNotNull(savedUser);
        }
    }

    @Test
    void save_duplicateEmail_ThrowRuntimeException () {
        User validUser = new User("Test", "test@example.com", 20, LocalDateTime.now());
        userDao.save(validUser);
        User userWithDuplicateEmail = new User ("Test_2", "test@example.com", 25, LocalDateTime.now());
        assertThrows(RuntimeException.class, () -> userDao.save(userWithDuplicateEmail));
    }

    @Test
    void update_validUserChanges_userUpdated () {
        User originalUser = new User ("Test", "original@example.com", 20, LocalDateTime.now());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(originalUser);
            transaction.commit();
        }
        originalUser.setEmail("updated@example.com");
        userDao.update(originalUser);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User updatedUser = session.find(User.class, originalUser.getId());
            assertEquals("updated@example.com", updatedUser.getEmail());
        }
    }

    @Test
    void update_notPersistedUser_ThrowEntityNotFoundException () {
        User notPersistedUser = new User("Test", "original@example.com", 20, LocalDateTime.now());
        notPersistedUser.setEmail("updated@example.com");
        assertThrows(EntityNotFoundException.class, () -> userDao.update(notPersistedUser));
    }

    @Test
    void delete_validUser_userDeleted () {
        User toDeleteUser = new User ("Test", "test@example.com", 20, LocalDateTime.now());
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(toDeleteUser);
            transaction.commit();
        }
        userDao.delete(toDeleteUser);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            User deletedUser = session.find(User.class, toDeleteUser.getId());
            assertNull(deletedUser);
        }
    }
}