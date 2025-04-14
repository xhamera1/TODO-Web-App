package com.todo.rails.elite.starter.code.controller;

import com.todo.rails.elite.starter.code.model.Task;
import com.todo.rails.elite.starter.code.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

	private final TaskService taskService;

	@Autowired
	public TaskController(TaskService taskService) {
		this.taskService = taskService;
	}

	@GetMapping("/all")
	public ResponseEntity<List<Task>> getAllTasks() {
		try {
			return ResponseEntity.ok(taskService.getAllTasks());
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Task> getTaskById(@PathVariable(name = "id") Long id) {
		try {
			return ResponseEntity.ok(taskService.getTaskById(id));
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/title/{title}")
	public ResponseEntity<Task> getTaskByTitle(@PathVariable(name = "title") String title) {
		try {
			return ResponseEntity.ok(taskService.getTaskByTitle(title));
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ModelAndView addTask(@ModelAttribute Task task) {
		try {
			Task addedTask = taskService.addTask(task);
			return new ModelAndView("redirect:/tasks");
		} catch (Exception exception) {
			return new ModelAndView("redirect:/tasks/add", "task", task);
		}
	}

	@RequestMapping(value = "/update/{id}", method = RequestMethod.GET)
	public ModelAndView updateTask(@PathVariable(name = "id") Long id) {
		Task taskById = taskService.getTaskById(id);
		if (taskById != null) {
			taskById.setDueDate(
					LocalDate.parse(
							DateTimeFormatter.ofPattern("yyyy-MM-dd")
									.format(
											taskById.getDueDate()
									)
					)
			);
			return new ModelAndView("edit", "task", taskById);
		} else {
			throw new RuntimeException("Task not found");
		}
	}

	@PostMapping("/update")
	public ModelAndView updateTask(@ModelAttribute Task task) {
		try {
			Task updatedTask = taskService.updateTask(task);
			return new ModelAndView("redirect:/tasks", "task", updatedTask);
		} catch (Exception exception) {
			throw new RuntimeException("Task not found");
		}
	}

	@PostMapping("/complete/{id}")
	public ModelAndView completeTask(@PathVariable Long id) {
		try {
			Task taskById = taskService.getTaskById(id);
			taskById.setCompleted(true);
			taskService.updateTask(taskById);
			return new ModelAndView("redirect:/");
		} catch (Exception exception) {
			return new ModelAndView("redirect:/");
		}
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	public ModelAndView deleteTask(@PathVariable Long id) {
		try {
			Task taskById = taskService.getTaskById(id);
			taskService.deleteTask(taskById);
			return new ModelAndView("redirect:/");
		} catch (Exception exception) {
			return new ModelAndView("redirect:/");
		}
	}

	@GetMapping("/pending")
	public ResponseEntity<List<Task>> getPendingTasks() {
		try {
			return ResponseEntity.ok(taskService.getPendingTasks());
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/completed")
	public ResponseEntity<List<Task>> getCompletedTasks() {
		try {
			return ResponseEntity.ok(taskService.getCompletedTasks());
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/today")
	public ResponseEntity<List<Task>> getTodayTasks() {
		try {
			return ResponseEntity.ok(taskService.getTodayTasks());
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}
}
