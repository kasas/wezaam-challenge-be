package com.wezaam.withdrawal.rest;

import com.wezaam.withdrawal.model.User;
import com.wezaam.withdrawal.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    void itShouldCheckThatFindUsers() {
        //given default database
        UserController controller = new UserController(userRepository);
        //when
        List<User> users = controller.findAll();
        //then
        assertNotNull(users);
        assertTrue(users.size() > 0);
    }

    @Test
    void itShouldCheckThatUserExist() {
        //given default database
        UserController controller = new UserController(userRepository);
        //when
        User result = controller.findById(1L);
        //then
        assertNotNull(result);
        assertEquals(1L, result.getId());

    }
    @Test
    void itShouldCheckThatUserNotExist() {
        //given
        UserController controller = new UserController(userRepository);
        //when
        User item = null;
        try {
            item = controller.findById(-1L);
        }
        catch (Exception e){
            assertTrue( e instanceof  NoSuchElementException);
        }
        //then
        assertNull(item);
    }
}