package com.todo.rails.elite.starter.code.service;

import com.todo.rails.elite.starter.code.exceptions.ResourceAlreadyExistsException;
import com.todo.rails.elite.starter.code.exceptions.ResourceNotFoundException;
import com.todo.rails.elite.starter.code.model.User;
import com.todo.rails.elite.starter.code.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void addUserTest_Success() {
        User userToAdd = new User("Test username", "TestPassword", "testemail@gmail.com", "USER");
        User savedUser = new User("Test username", "encodedPassword", "testemail@gmail.com", "USER");
        savedUser.setId(1L);

        when(userRepository.findByUsername("Test username")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("testemail@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("TestPassword")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User resultUser = null;
        try {
            resultUser = userService.addUser(userToAdd);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }

        assertNotNull(resultUser);
        assertEquals(1L, resultUser.getId());
        assertEquals("Test username", resultUser.getUsername());
        assertEquals("encodedPassword", resultUser.getPassword());

        verify(userRepository, times(1)).findByUsername("Test username");
        verify(userRepository, times(1)).findByEmail("testemail@gmail.com");
        verify(passwordEncoder, times(1)).encode("TestPassword");
        verify(userRepository, times(1)).save(userToAdd);
    }

    @Test
    void addUserTest_UsernameExists() {
        User userToAdd = new User("Existing username", "TestPassword", "testemail@gmail.com", "USER");
        User existingUser = new User("Existing username", "otherPassword", "otheremail@gmail.com", "USER");

        when(userRepository.findByUsername("Existing username")).thenReturn(Optional.of(existingUser));

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> userService.addUser(userToAdd));

        assertEquals("User with username '" + userToAdd.getUsername()+ "' already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("Existing username");
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void addUserTest_EmailExists() {
        User userToAdd = new User("Test username", "TestPassword", "existingemail@gmail.com", "USER");
        User existingUser = new User("otherUsername", "otherPassword", "existingemail@gmail.com", "USER");

        when(userRepository.findByUsername("Test username")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existingemail@gmail.com")).thenReturn(Optional.of(existingUser));

        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class, () -> userService.addUser(userToAdd));

        assertEquals("User with email '" + userToAdd.getEmail() + "' already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("Test username");
        verify(userRepository, times(1)).findByEmail("existingemail@gmail.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserByUsername_Success() {
        String usernameToFind = "Test username";
        User foundUser = new User(usernameToFind, "encodedPassword", "testemail@gmail.com", "USER");
        foundUser.setId(1L);

        when(userRepository.findByUsername(usernameToFind)).thenReturn(Optional.of(foundUser));

        User resultUser = null;
        try {
            resultUser = userService.getUserByUsername(usernameToFind);
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }

        assertNotNull(resultUser);
        assertEquals(1L, resultUser.getId());
        assertEquals(usernameToFind, resultUser.getUsername());
        verify(userRepository, times(1)).findByUsername(usernameToFind);
    }

    @Test
    void getUserByUsername_NotFound() {
        String usernameToFind = "NonExistentUser";

        when(userRepository.findByUsername(usernameToFind)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername(usernameToFind));

        assertEquals("User not found with username: " + usernameToFind, exception.getMessage());
        verify(userRepository, times(1)).findByUsername(usernameToFind);
    }

    @Test
    void getUserByEmail_Success() {
        String emailToFind = "testemail@gmail.com";
        User foundUser = new User("Test username", "encodedPassword", emailToFind, "USER");
        foundUser.setId(1L);

        when(userRepository.findByEmail(emailToFind)).thenReturn(Optional.of(foundUser));

        User resultUser = userService.getUserByEmail(emailToFind);

        assertNotNull(resultUser);
        assertEquals(1L, resultUser.getId());
        assertEquals(emailToFind, resultUser.getEmail());
        verify(userRepository, times(1)).findByEmail(emailToFind);
    }

    @Test
    void getUserByEmail_NotFound() {
        String emailToFind = "nonexistent@gmail.com";

        when(userRepository.findByEmail(emailToFind)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserByEmail(emailToFind));

        assertEquals("User not found with email: " + emailToFind, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(emailToFind);
    }

    @Test
    void getUserById_Success() {
        Long idToFind = 1L;
        User foundUser = new User("Test username", "encodedPassword", "testemail@gmail.com", "USER");
        foundUser.setId(idToFind);

        when(userRepository.findById(idToFind)).thenReturn(Optional.of(foundUser));

        User resultUser = userService.getUserById(idToFind);

        assertNotNull(resultUser);
        assertEquals(idToFind, resultUser.getId());
        verify(userRepository, times(1)).findById(idToFind);
    }

    @Test
    void getUserById_NotFound() {
        Long idToFind = 99L;

        when(userRepository.findById(idToFind)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getUserById(idToFind));

        assertEquals("User not found with ID: " + idToFind, exception.getMessage());
        verify(userRepository, times(1)).findById(idToFind);
    }

    @Test
    void updateUser_Success() {
        User existingUser = new User("Old username", "oldPassword", "oldemail@gmail.com", "USER");
        User updatedUser = new User("New username", "newPassword", "newemail@gmail.com", "ADMIN");
        existingUser.setId(1L);
        updatedUser.setId(1L);

        when(userRepository.findByUsername("New username")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        User resultUser = userService.updateUser(updatedUser);

        assertNotNull(resultUser);
        assertEquals(1L, resultUser.getId());
        assertEquals("New username", resultUser.getUsername());
        assertEquals("newPassword", resultUser.getPassword());
        assertEquals("newemail@gmail.com", resultUser.getEmail());
        assertEquals("ADMIN", resultUser.getRoles());

        verify(userRepository, times(1)).findByUsername("New username");
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void updateUser_NotFound() {
        User userToUpdate = new User("NonExistentUser", "newPassword", "newemail@gmail.com", "ADMIN");

        when(userRepository.findByUsername("NonExistentUser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.updateUser(userToUpdate));

        assertEquals("User not found for update with username: NonExistentUser", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("NonExistentUser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        User userToDelete = new User("Test username", "password", "email@gmail.com", "USER");
        userToDelete.setId(1L);

        when(userRepository.findByUsername("Test username")).thenReturn(Optional.of(userToDelete));
        doNothing().when(userRepository).delete(userToDelete);

        userService.deleteUser(userToDelete);

        verify(userRepository, times(1)).findByUsername("Test username");
        verify(userRepository, times(1)).delete(userToDelete);
    }

    @Test
    void deleteUser_NotFound() {
        User userToDelete = new User("NonExistentUser", "password", "email@gmail.com", "USER");

        when(userRepository.findByUsername("NonExistentUser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.deleteUser(userToDelete));

        assertEquals("User not found for deletion with username: NonExistentUser", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("NonExistentUser");
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(
                new User("user1", "pass1", "email1@gmail.com", "USER"),
                new User("user2", "pass2", "email2@gmail.com", "ADMIN")
        );
        users.get(0).setId(1L);
        users.get(1).setId(1L);

        when(userRepository.findAll()).thenReturn(users);

        List<User> resultUsers = userService.getAllUsers();

        assertNotNull(resultUsers);
        assertEquals(2, resultUsers.size());
        assertEquals("user1", resultUsers.get(0).getUsername());
        assertEquals("user2", resultUsers.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_NotFound() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getAllUsers());

        assertEquals("No users found", exception.getMessage());
        verify(userRepository, times(1)).findAll();
    }
}