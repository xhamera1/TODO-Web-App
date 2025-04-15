package com.todo.rails.elite.starter.code.controller;

import com.todo.rails.elite.starter.code.model.Task;
import com.todo.rails.elite.starter.code.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void getAllTasks_Success() throws Exception {
        Task task1 = new Task("Task 1", null, false, LocalDate.now());
        task1.setId(1L);
        Task task2 = new Task("Task 2", null, true, LocalDate.now().plusDays(1));
        task2.setId(2L);
        List<Task> tasks = Arrays.asList(task1, task2);
        when(taskService.getAllTasks()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Task 1")));
    }

    @Test
    void getAllTasks_NotFound() throws Exception {
        when(taskService.getAllTasks()).thenThrow(new RuntimeException("No tasks found"));

        mockMvc.perform(get("/api/tasks/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskById_Success() throws Exception {
        Task task = new Task("Task 1", null, false, LocalDate.now());
        task.setId(1L);
        when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Task 1")));
    }

    @Test
    void getTaskById_NotFound() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(new RuntimeException("Task not found"));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTaskByTitle_Success() throws Exception {
        Task task = new Task("Test Task", null, false, LocalDate.now());
        task.setId(1L);
        when(taskService.getTaskByTitle("Test Task")).thenReturn(task);

        mockMvc.perform(get("/api/tasks/title/Test Task"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Test Task")));
    }

    @Test
    void getTaskByTitle_NotFound() throws Exception {
        when(taskService.getTaskByTitle("NonExistent Task")).thenThrow(new RuntimeException("Task not found"));

        mockMvc.perform(get("/api/tasks/title/NonExistent Task"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addTask_Success() throws Exception {
        Task taskToAdd = new Task("New Task", null, false, LocalDate.now());
        Task addedTask = new Task("New Task", null, false, LocalDate.now());
        addedTask.setId(3L);
        when(taskService.addTask(org.mockito.ArgumentMatchers.any(Task.class))).thenReturn(addedTask);

        mockMvc.perform(post("/api/tasks/add")
                        .flashAttr("task", taskToAdd))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        verify(taskService, times(1)).addTask(taskToAdd);
    }

    @Test
    void addTask_Failure() throws Exception {
        Task taskToAdd = new Task("New Task", null, false, LocalDate.now());
        when(taskService.addTask(org.mockito.ArgumentMatchers.any(Task.class))).thenThrow(new RuntimeException("Failed to add task"));

        mockMvc.perform(post("/api/tasks/add")
                        .flashAttr("task", taskToAdd))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/add"))
                .andExpect(model().attribute("task", taskToAdd));

        verify(taskService, times(1)).addTask(taskToAdd);
    }

    @Test
    void updateTask_Get_Success() throws Exception {
        LocalDate dueDate = LocalDate.now().plusDays(2);
        Task task = new Task("Existing Task", null, false, dueDate);
        task.setId(1L);
        when(taskService.getTaskById(1L)).thenReturn(task);

        mockMvc.perform(get("/api/tasks/update/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("edit"))
                .andExpect(model().attributeExists("task"))
                .andExpect(model().attribute("task", hasProperty("id", is(1L))))
                .andExpect(model().attribute("task", hasProperty("title", is("Existing Task"))))
                .andExpect(model().attribute("task", hasProperty("dueDate", is(dueDate))));
    }


    @Test
    void updateTask_Post_Success() throws Exception {
        Task taskToUpdate = new Task("Updated Task", null, true, LocalDate.now().plusDays(3));
        taskToUpdate.setId(1L);
        Task updatedTask = new Task("Updated Task", null, true, LocalDate.now().plusDays(3));
        updatedTask.setId(1L);
        when(taskService.updateTask(org.mockito.ArgumentMatchers.any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(post("/api/tasks/update")
                        .flashAttr("task", taskToUpdate))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"))
                .andExpect(model().attribute("task", updatedTask));

        verify(taskService, times(1)).updateTask(taskToUpdate);
    }


    @Test
    void completeTask_Success() throws Exception {
        Task taskToComplete = new Task("Task to Complete", null, false, LocalDate.now());
        taskToComplete.setId(1L);
        Task completedTask = new Task("Task to Complete", null, true, LocalDate.now());
        completedTask.setId(1L);
        when(taskService.getTaskById(1L)).thenReturn(taskToComplete);
        when(taskService.updateTask(org.mockito.ArgumentMatchers.any(Task.class))).thenReturn(completedTask);

        mockMvc.perform(post("/api/tasks/complete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskService, times(1)).getTaskById(1L);
        verify(taskService, times(1)).updateTask(argThat(task -> task.getId().equals(1L) && task.getTitle().equals("Task to Complete") && task.isCompleted()));
    }

    @Test
    void completeTask_Failure() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(new RuntimeException("Task not found"));

        mockMvc.perform(post("/api/tasks/complete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskService, times(1)).getTaskById(1L);
        verify(taskService, never()).updateTask(org.mockito.ArgumentMatchers.any(Task.class));
    }

    @Test
    void deleteTask_Success() throws Exception {
        Task taskToDelete = new Task("Task to Delete", null, false, LocalDate.now());
        taskToDelete.setId(1L);
        when(taskService.getTaskById(1L)).thenReturn(taskToDelete);
        doNothing().when(taskService).deleteTask(taskToDelete);

        mockMvc.perform(delete("/api/tasks/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskService, times(1)).getTaskById(1L);
        verify(taskService, times(1)).deleteTask(taskToDelete);
    }

    @Test
    void deleteTask_Failure() throws Exception {
        when(taskService.getTaskById(1L)).thenThrow(new RuntimeException("Task not found"));

        mockMvc.perform(delete("/api/tasks/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(taskService, times(1)).getTaskById(1L);
        verify(taskService, never()).deleteTask(org.mockito.ArgumentMatchers.any(Task.class));
    }

    @Test
    void getPendingTasks_Success() throws Exception {
        Task pendingTask = new Task("Pending Task", null, false, LocalDate.now());
        pendingTask.setId(1L);
        List<Task> pendingTasks = Collections.singletonList(pendingTask);
        when(taskService.getPendingTasks()).thenReturn(pendingTasks);

        mockMvc.perform(get("/api/tasks/pending"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Pending Task")))
                .andExpect(jsonPath("$[0].completed", is(false)));
    }

    @Test
    void getPendingTasks_NotFound() throws Exception {
        when(taskService.getPendingTasks()).thenThrow(new RuntimeException("No pending tasks found"));

        mockMvc.perform(get("/api/tasks/pending"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCompletedTasks_Success() throws Exception {
        Task completedTask = new Task("Completed Task", null, true, LocalDate.now().minusDays(1));
        completedTask.setId(2L);
        List<Task> completedTasks = Collections.singletonList(completedTask);
        when(taskService.getCompletedTasks()).thenReturn(completedTasks);

        mockMvc.perform(get("/api/tasks/completed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Completed Task")))
                .andExpect(jsonPath("$[0].completed", is(true)));
    }

    @Test
    void getCompletedTasks_NotFound() throws Exception {
        when(taskService.getCompletedTasks()).thenThrow(new RuntimeException("No completed tasks found"));

        mockMvc.perform(get("/api/tasks/completed"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTodayTasks_Success() throws Exception {
        Task todayTask = new Task("Today's Task", null, false, LocalDate.now());
        todayTask.setId(3L);
        List<Task> todayTasks = Collections.singletonList(todayTask);
        when(taskService.getTodayTasks()).thenReturn(todayTasks);

        mockMvc.perform(get("/api/tasks/today"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Today's Task")));
    }

    @Test
    void getTodayTasks_NotFound() throws Exception {
        when(taskService.getTodayTasks()).thenThrow(new RuntimeException("No tasks for today found"));

        mockMvc.perform(get("/api/tasks/today"))
                .andExpect(status().isNotFound());
    }
}