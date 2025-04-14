package com.todo.rails.elite.starter.code.service;

import com.todo.rails.elite.starter.code.model.Task;
import com.todo.rails.elite.starter.code.repository.TaskRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for handling business logic related to Tasks.
 * Provides methods for creating, retrieving, updating, and deleting tasks,
 * as well as retrieving filtered lists of tasks.
 */
@Service
public class TaskService {

	private static final Logger log = (Logger) LoggerFactory.getLogger(TaskService.class);
	private final TaskRepository taskRepository;

	/**
	 * Constructs a TaskService with the necessary TaskRepository dependency.
	 *
	 * @param taskRepository The repository for task data access.
	 */
	@Autowired
	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
	}

	/**
	 * Adds a new task to the repository.
	 * Checks if a task with the same title already exists before saving.
	 *
	 * @param task The task object to add. Must not be null.
	 * @return The saved task object with its generated ID.
	 * @throws RuntimeException if a task with the same title already exists.
	 */
	public Task addTask(@NotNull(message = "Task cannot be null") Task task) throws RuntimeException {
		if (taskRepository.findByTitle(task.getTitle()).isPresent()) {
			log.warn("Attempted to add task with existing title: {}", task.getTitle());
			throw new RuntimeException("Task already exists with title: " + task.getTitle());
		}
		log.info("Adding new task with title: {}", task.getTitle());
		return taskRepository.save(task);
	}

	/**
	 * Retrieves a task by its unique ID.
	 *
	 * @param id The ID of the task to retrieve. Must not be null.
	 * @return The found task object.
	 * @throws RuntimeException if no task is found with the given ID.
	 */
	public Task getTaskById(@NotNull(message = "Id cannot be null") Long id) throws RuntimeException {
		log.debug("Attempting to get task by id: {}", id);
		return taskRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("Task not found with id: {}", id);
					return new RuntimeException("Task not found with ID: " + id);
				});
	}

	/**
	 * Retrieves a task by its unique title.
	 *
	 * @param title The title of the task to retrieve. Must not be null or blank.
	 * @return The found task object.
	 * @throws RuntimeException if no task is found with the given title.
	 */
	public Task getTaskByTitle(
			@NotNull(message = "Title cannot be null")
			@NotBlank(message = "Title cannot be blank")
			String title
	) throws RuntimeException {
		log.debug("Attempting to get task by title: {}", title);
		return taskRepository.findByTitle(title)
				.orElseThrow(() -> {
					log.warn("Task not found with title: {}", title);
					return new RuntimeException("Task not found with title: " + title);
				});
	}

	/**
	 * Retrieves all tasks from the repository.
	 *
	 * @return A list of all tasks, or an empty list if none exist.
	 */
	public List<Task> getAllTasks() {
		log.debug("Retrieving all tasks.");
		List<Task> tasks = taskRepository.findAll();
		if (tasks.isEmpty()) {
			log.info("No tasks found in repository.");
			return List.of(); // Return an immutable empty list
		}
		return tasks;
	}

	/**
	 * Updates an existing task identified by the title within the provided task object.
	 * Finds the task by title, updates its fields, and saves the changes.
	 *
	 * @param task The task object containing the updated information and the title of the task to update. Must not be null.
	 * @return The updated task object.
	 * @throws RuntimeException if no task is found with the title specified in the input task object.
	 */
	public Task updateTask(@NotNull(message = "Task cannot be null") Task task) throws RuntimeException {
		Optional<Task> existingTaskOptional = taskRepository.findByTitle(task.getTitle());

		if (existingTaskOptional.isEmpty()) {
			log.warn("Attempting to update not-existent taks with title: {}", task.getTitle());
			throw new RuntimeException("Task not found for update with title: " + task.getTitle());
		}

		log.info("Updating task with id: {}", task.getId());
		Task taskToUpdate = existingTaskOptional.get();

		taskToUpdate.setTitle(task.getTitle());
		taskToUpdate.setDescription(task.getDescription());
		taskToUpdate.setCompleted(task.isCompleted());
		taskToUpdate.setDueDate(task.getDueDate());

		return taskRepository.save(taskToUpdate);
	}

	/**
	 * Deletes a task from the repository.
	 * Finds the task by title before attempting deletion.
	 *
	 * @param task The task object to delete. It must contain the title of the task to be deleted and must not be null.
	 * @throws RuntimeException if no task is found with the title specified in the input task object.
	 */
	public void deleteTask(@NotNull(message = "Task cannot be null") Task task) throws RuntimeException {
		Optional<Task> taskToDeleteOptional = taskRepository.findByTitle(task.getTitle());

		if (taskToDeleteOptional.isEmpty()) {
			log.warn("Attempting to delete not existing task with title: {}", task.getTitle());
			throw new RuntimeException("Task not found for deletion with title: " + task.getTitle());
		}
		log.info("Deleting task with id: {}", task.getId());
		// It's safer to delete by ID if available, or use the found entity
		Task taskToDelete = taskToDeleteOptional.get();
		taskRepository.delete(taskToDelete); // Delete the actual entity found
	}

	/**
	 * Retrieves a list of all tasks that are not yet completed.
	 *
	 * @return A list of pending tasks, or an empty list if none are found.
	 */
	public List<Task> getPendingTasks() {
		log.debug("Retrieving pending tasks.");
		List<Task> allTasks = getAllTasks(); // Less efficient if many tasks exist
		if (allTasks.isEmpty()) {
			return List.of();
		}
		return allTasks.stream()
				.filter(task -> !task.isCompleted())
				.toList();
	}

	/**
	 * Retrieves a list of all tasks that have been completed.
	 *
	 * @return A list of completed tasks, or an empty list if none are found.
	 */
	public List<Task> getCompletedTasks() {
		log.debug("Retrieving completed tasks.");
		List<Task> allTasks = getAllTasks(); // Less efficient if many tasks exist
		if (allTasks.isEmpty()) {
			return List.of();
		}
		return allTasks.stream()
				.filter(Task::isCompleted)
				.toList();
	}

	/**
	 * Retrieves a list of tasks that are due today and are not yet completed.
	 *
	 * @return A list of pending tasks due today, or an empty list if none are found.
	 */
	public List<Task> getTodayTasks() {
		log.debug("Retrieving task due to today.");
		LocalDate today = LocalDate.now();
		List<Task> allTasks = getAllTasks();
		if (allTasks.isEmpty()) {
			return List.of();
		}
		return allTasks.stream()
				.filter(task -> !task.isCompleted()) // Filter for pending tasks
				.filter(task -> task.getDueDate() != null && task.getDueDate().isEqual(today)) // Filter for today's due date
				.toList();
	}
}
