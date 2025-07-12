import org.example.dao.UserDao;
import org.example.models.User;
import org.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void initTestUser() {
        testUser = new User("Test", "test@example.com", 23, LocalDateTime.now());
    }

    @Test
    void findById_validId_returnUser() {
        when(userDao.findById(1)).thenReturn(testUser);
        User resultUser = userService.findById(1);
        assertNotNull(resultUser);
        assertEquals(testUser, resultUser);
    }

    @Test
    void findById_invalidId_returnNull() {
        when(userDao.findById(-1)).thenThrow(new RuntimeException());
        User resultUser = userService.findById(-1);
        assertNull(resultUser);
    }

    @Test
    void findByEmail_validEmail_returnUser() {
        when(userDao.findByEmail(testUser.getEmail())).thenReturn(testUser);
        User resultUser = userService.findByEmail("test@example.com");
        assertNotNull(resultUser);
        assertEquals(resultUser, testUser);
    }

    @Test
    void findByEmail_invalidEmail_returnNull() {
        when(userDao.findByEmail(" ")).thenThrow(new RuntimeException());
        User resultUser = userService.findByEmail(" ");
        assertNull(resultUser);
    }

    @Test
    void findAll_notEmptyUsersList_returnUsersList() {
        List<User> users = Arrays.asList(
                testUser,
                new User("Test2", "test2@example.com", 37, LocalDateTime.now())
        );
        when(userDao.findAll()).thenReturn(users);
        List<User> resultUsers = userService.findAll();
        assertNotNull(resultUsers);
        assertEquals(2, resultUsers.size());
        assertEquals(resultUsers, users);
    }

    @Test
    void findAll_DaoThrowsException_returnNull() {
        when(userDao.findAll()).thenThrow(new RuntimeException());
        List<User> resultUsers = userService.findAll();
        assertNull(resultUsers);
    }

    @Test
    void save_anyUser_DaoMethodCalledOneTime() {
        userService.save(testUser);
        verify(userDao, times(1)).save(testUser);
    }

    @Test
    void update_anyUser_DaoMethodCalledOneTime() {
        userService.update(testUser);
        verify(userDao, times(1)).update(testUser);
    }

    @Test
    void delete_validUser_DaoMethodCalledOneTime(){
        userService.delete(testUser);
        verify(userDao, times(1)).delete(testUser);
    }

    @Test
    void delete_DaoThrowsException_ErrorMessage() {
        doThrow(new RuntimeException()).when(userDao).delete(testUser);
        ByteArrayOutputStream errorMessage = new ByteArrayOutputStream();
        System.setOut(new PrintStream(errorMessage));
        userService.delete(testUser);
        assertTrue(errorMessage.toString().startsWith("Error: "));
        System.setOut(System.out);
    }
}
