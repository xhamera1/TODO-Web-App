package com.todo.rails.elite.starter.code.controller;

import com.todo.rails.elite.starter.code.model.User;
import com.todo.rails.elite.starter.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/all")
	public ResponseEntity<Iterable<User>> getAllUsers() {
		try {
			Iterable<User> allUsers = userService.getAllUsers();
			return ResponseEntity.ok(allUsers);
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable(name = "id") Long id) {
		try {
			User userById = userService.getUserById(id);
			return ResponseEntity.ok(userById);
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/username/{username}")
	public ResponseEntity<User> getUseByUsername(@PathVariable(name = "username") String username) {
		try {
			User userByUsername = userService.getUserByUsername(username);
			return ResponseEntity.ok(userByUsername);
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}


	@GetMapping("/email/{email}")
	public ResponseEntity<User> getUserByEmail(@PathVariable(name = "email") String email) {
		try {
			User userByEmail = userService.getUserByEmail(email);
			return ResponseEntity.ok(userByEmail);
		} catch (Exception exception) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/update")
	public ResponseEntity<String> updateUser(@RequestBody User user) {
		try {
			User updatedUser = userService.updateUser(user);
			return ResponseEntity.ok("User: " + updatedUser.getUsername() + " updated successfully");
		} catch (Exception exception) {
			return ResponseEntity.badRequest().body(exception.getLocalizedMessage());
		}
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteUser(@RequestBody User user) {
		try {
			userService.deleteUser(user);
			return ResponseEntity.ok("User: " + user.getUsername() + " deleted successfully");
		} catch (Exception exception) {
			return ResponseEntity.badRequest().body(exception.getLocalizedMessage());
		}
	}
}
