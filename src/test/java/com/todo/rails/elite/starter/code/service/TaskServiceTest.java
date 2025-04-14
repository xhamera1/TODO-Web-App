package com.todo.rails.elite.starter.code.service;

import com.todo.rails.elite.starter.code.model.Task;
import com.todo.rails.elite.starter.code.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;


    @Test
    void addTaskTest_Success() {
        Task taskToAdd = new Task("Test title", "Test Desc", false, LocalDate.now());
        Task savedTask = new Task("Test title", "Test Desc", false, LocalDate.now());
        savedTask.setId(1L);

        when(taskRepository.findByTitle("Test title")).thenReturn(Optional.empty());
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task resultTask = taskService.addTask(taskToAdd);

        assertNotNull(resultTask);
        assertEquals(1L, resultTask.getId());
        assertEquals("Test title", resultTask.getTitle());
        verify(taskRepository, times(1)).findByTitle("Test title");
        verify(taskRepository, times(1)).save(taskToAdd);
    }

    @Test
    void addTaskTest_AlreadyExists() {
        Task taskToAdd = new Task("Existing Title", "Test Desc", false, LocalDate.now());
        Task existingTask = new Task("Existing Title", "Old Desc", true, LocalDate.now().minusDays(1));

        when(taskRepository.findByTitle("Existing Title")).thenReturn(Optional.of(existingTask));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.addTask(taskToAdd);
        });

        assertEquals("Task already exists with title: Existing Title", exception.getMessage());
        verify(taskRepository, times(1)).findByTitle("Existing Title");
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTaskById_Found() {
        Long taskId = 1L;
        Task expectedTask = new Task("Found Task", "Desc", false, LocalDate.now());
        expectedTask.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(expectedTask));

        Task actualTask = taskService.getTaskById(taskId);

        assertNotNull(actualTask);
        assertEquals(taskId, actualTask.getId());
        assertEquals("Found Task", actualTask.getTitle());
        verify(taskRepository, times(1)).findById(taskId); // Verify findById was called
    }

    @Test
    void getTaskById_NotFound() {
        Long taskId = 99L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            taskService.getTaskById(taskId);
        });

        assertEquals("Task not found with ID: " + taskId, exception.getMessage());
        verify(taskRepository, times(1)).findById(taskId);
    }

    @Test
    void getTaskByTitle_Found() {
        // Arrange
        String title = "Find Me";
        Task expectedTask = new Task(title, "Desc", false, LocalDate.now());
        expectedTask.setId(1L);
        when(taskRepository.findByTitle(title)).thenReturn(Optional.of(expectedTask));

        Task actualTask = taskService.getTaskByTitle(title);

        assertNotNull(actualTask);
        assertEquals(title, actualTask.getTitle());
        assertEquals(1L, actualTask.getId());
        verify(taskRepository).findByTitle(title);
    }

    @Test
    void getTaskByTitle_NotFound() {
        String title = "NonExistent";
        when(taskRepository.findByTitle(title)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.getTaskByTitle(title));
        assertEquals("Task not found with title: " + title, exception.getMessage());
        verify(taskRepository).findByTitle(title);
    }

    @Test
    void getAllTasks_ReturnsTasks() {
        Task task1 = new Task("Task 1", "Desc 1", false, LocalDate.now());
        Task task2 = new Task("Task 2", "Desc 2", true, LocalDate.now().minusDays(1));
        List<Task> expectedTasks = Arrays.asList(task1, task2);
        when(taskRepository.findAll()).thenReturn(expectedTasks);

        List<Task> actualTasks = taskService.getAllTasks();

        assertNotNull(actualTasks);
        assertEquals(2, actualTasks.size());
        assertEquals(expectedTasks, actualTasks);
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getAllTasks_ReturnsEmptyList() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        List<Task> actualTasks = taskService.getAllTasks();

        assertNotNull(actualTasks);
        assertTrue(actualTasks.isEmpty());
        verify(taskRepository, times(1)).findAll(); // Only called once if the first call returns empty
    }

    @Test
    void updateTask_Success() {
        String title = "Update Me";
        Task existingTask = new Task(title, "Old Desc", false, LocalDate.now());
        existingTask.setId(1L);

        Task updatedData = new Task(title, "New Desc", true, LocalDate.now().plusDays(1));
        updatedData.setId(1L); // ID should match for update

        when(taskRepository.findByTitle(title)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task taskToSave = invocation.getArgument(0);
            // Simulate saving by returning a copy or the same object with potential ID set
            taskToSave.setId(existingTask.getId()); // Ensure ID is preserved/set
            return taskToSave;
        });


        Task result = taskService.updateTask(updatedData);

        assertNotNull(result);
        assertEquals(existingTask.getId(), result.getId()); // ID should remain the same
        assertEquals(updatedData.getTitle(), result.getTitle());
        assertEquals(updatedData.getDescription(), result.getDescription());
        assertEquals(updatedData.isCompleted(), result.isCompleted());
        assertEquals(updatedData.getDueDate(), result.getDueDate());

        verify(taskRepository).findByTitle(title);
        verify(taskRepository).save(argThat(task ->
                task.getId().equals(existingTask.getId()) &&
                        task.getDescription().equals(updatedData.getDescription()) &&
                        task.isCompleted() == updatedData.isCompleted() &&
                        task.getDueDate().equals(updatedData.getDueDate())
        ));
    }


    @Test
    void updateTask_NotFound() {
        String title = "NonExistent";
        Task taskToUpdate = new Task(title, "New Desc", true, LocalDate.now());
        when(taskRepository.findByTitle(title)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.updateTask(taskToUpdate));
        assertEquals("Task not found for update with title: " + title, exception.getMessage());
        verify(taskRepository).findByTitle(title);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void deleteTask_Success() {
        String title = "Delete Me";
        Task taskToDelete = new Task(title, "Desc", false, LocalDate.now());
        taskToDelete.setId(1L);
        Task taskArgument = new Task(title, "Desc", false, LocalDate.now()); // Object passed to service method

        when(taskRepository.findByTitle(title)).thenReturn(Optional.of(taskToDelete));
        doNothing().when(taskRepository).delete(any(Task.class));

        taskService.deleteTask(taskArgument);

        verify(taskRepository).findByTitle(title);
        verify(taskRepository).delete(taskToDelete);
    }

    @Test
    void deleteTask_NotFound() {
        String title = "NonExistent";
        Task taskToDelete = new Task(title, "Desc", false, LocalDate.now());
        when(taskRepository.findByTitle(title)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> taskService.deleteTask(taskToDelete));
        assertEquals("Task not found for deletion with title: " + title, exception.getMessage());
        verify(taskRepository).findByTitle(title);
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void getPendingTasks_ReturnsPendingOnly() {
        Task pending1 = new Task("Pending 1", "Desc", false, LocalDate.now());
        Task completed1 = new Task("Completed 1", "Desc", true, LocalDate.now());
        Task pending2 = new Task("Pending 2", "Desc", false, LocalDate.now().plusDays(1));
        List<Task> allTasks = Arrays.asList(pending1, completed1, pending2);

        when(taskRepository.findAll()).thenReturn(allTasks);

        List<Task> pendingTasks = taskService.getPendingTasks();

        assertNotNull(pendingTasks);
        assertEquals(2, pendingTasks.size());
        assertTrue(pendingTasks.contains(pending1));
        assertTrue(pendingTasks.contains(pending2));
        assertFalse(pendingTasks.contains(completed1));
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getPendingTasks_ReturnsEmptyWhenNonePending() {
        Task completed1 = new Task("Completed 1", "Desc", true, LocalDate.now());
        Task completed2 = new Task("Completed 2", "Desc", true, LocalDate.now());
        List<Task> allTasks = Arrays.asList(completed1, completed2);

        when(taskRepository.findAll()).thenReturn(allTasks);

        List<Task> pendingTasks = taskService.getPendingTasks();

        assertNotNull(pendingTasks);
        assertTrue(pendingTasks.isEmpty());
        verify(taskRepository, times(1)).findAll(); // Called by getAllTasks
    }

    @Test
    void getCompletedTasks_ReturnsCompletedOnly() {
        Task pending1 = new Task("Pending 1", "Desc", false, LocalDate.now());
        Task completed1 = new Task("Completed 1", "Desc", true, LocalDate.now());
        Task completed2 = new Task("Completed 2", "Desc", true, LocalDate.now().minusDays(1));
        List<Task> allTasks = Arrays.asList(pending1, completed1, completed2);

        when(taskRepository.findAll()).thenReturn(allTasks);

        List<Task> completedTasks = taskService.getCompletedTasks();

        assertNotNull(completedTasks);
        assertEquals(2, completedTasks.size());
        assertTrue(completedTasks.contains(completed1));
        assertTrue(completedTasks.contains(completed2));
        assertFalse(completedTasks.contains(pending1));
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getCompletedTasks_ReturnsEmptyWhenNoneCompleted() {
        Task pending1 = new Task("Pending 1", "Desc", false, LocalDate.now());
        Task pending2 = new Task("Pending 2", "Desc", false, LocalDate.now());
        List<Task> allTasks = Arrays.asList(pending1, pending2);

        when(taskRepository.findAll()).thenReturn(allTasks);

        List<Task> completedTasks = taskService.getCompletedTasks();

        assertNotNull(completedTasks);
        assertTrue(completedTasks.isEmpty());
        verify(taskRepository, times(1)).findAll();
    }


    @Test
    void getTodayTasks_ReturnsPendingDueToday() {

        LocalDate today = LocalDate.now();
        Task todayPending = new Task("Today Pending", "Desc", false, today);
        Task todayCompleted = new Task("Today Completed", "Desc", true, today);
        Task tomorrowPending = new Task("Tomorrow Pending", "Desc", false, today.plusDays(1));
        Task yesterdayPending = new Task("Yesterday Pending", "Desc", false, today.minusDays(1));

        List<Task> allTasks = Arrays.asList(todayPending, todayCompleted, tomorrowPending, yesterdayPending);
        when(taskRepository.findAll()).thenReturn(allTasks);

        List<Task> todayTasksResult = taskService.getTodayTasks();

        assertNotNull(todayTasksResult);
        assertEquals(1, todayTasksResult.size());
        assertTrue(todayTasksResult.contains(todayPending));
        assertFalse(todayTasksResult.contains(todayCompleted));
        assertFalse(todayTasksResult.contains(tomorrowPending));
        assertFalse(todayTasksResult.contains(yesterdayPending));
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getTodayTasks_ReturnsEmptyWhenNoneDueTodayOrPending() {
        LocalDate today = LocalDate.now();
        Task todayCompleted = new Task("Today Completed", "Desc", true, today);
        Task tomorrowPending = new Task("Tomorrow Pending", "Desc", false, today.plusDays(1));
        Task yesterdayPending = new Task("Yesterday Pending", "Desc", false, today.minusDays(1));

        List<Task> allTasks = Arrays.asList(todayCompleted, tomorrowPending, yesterdayPending);
        when(taskRepository.findAll()).thenReturn(allTasks);

        List<Task> todayTasksResult = taskService.getTodayTasks();

        assertNotNull(todayTasksResult);
        assertTrue(todayTasksResult.isEmpty());
        verify(taskRepository, times(1)).findAll(); // Called by getAllTasks
    }
}