package com.todo.rails.elite.starter.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.rails.elite.starter.code.model.User;
import com.todo.rails.elite.starter.code.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        user1 = new User("user1", "pass1", "user1@example.com", "ROLE_USER");
        user1.setId(1L);
        user2 = new User("user2", "pass2", "user2@example.com", "ROLE_ADMIN");
        user2.setId(2L);
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].username", is("user2")));
    }

    @Test
    void getAllUsers_NotFound() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk()) // Should return 200 OK with an empty list, not 404
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(user1);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("user1")));
    }

    @Test
    void getUseByUsername_Success() throws Exception {
        when(userService.getUserByUsername("user1")).thenReturn(user1);

        mockMvc.perform(get("/api/users/username/user1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("user1")));
    }


    @Test
    void getUserByEmail_Success() throws Exception {
        when(userService.getUserByEmail("user1@example.com")).thenReturn(user1);

        mockMvc.perform(get("/api/users/email/user1@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("user1")));
    }


    @Test
    void updateUser_Success() throws Exception {
        User updatedUser = new User("user1updated", "pass1updated", "user1updated@example.com", "ROLE_USER");
        updatedUser.setId(1L);
        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("User: user1updated updated successfully"));

        verify(userService, times(1)).updateUser(any(User.class));
    }

    @Test
    void updateUser_Failure() throws Exception {
        User userToUpdate = new User("user1updated", "pass1updated", "user1updated@example.com", "ROLE_USER");
        userToUpdate.setId(1L);
        when(userService.updateUser(any(User.class))).thenThrow(new RuntimeException("Failed to update user"));

        mockMvc.perform(put("/api/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userToUpdate)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed to update user"));

        verify(userService, times(1)).updateUser(any(User.class));
    }


}