package com.todo.rails.elite.starter.code.service;

import com.todo.rails.elite.starter.code.model.User;
import com.todo.rails.elite.starter.code.repository.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional; // Import Optional

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	/**
	 * Constructs the UserService with required dependencies.
	 *
	 * @param userRepository  The repository for user data access.
	 * @param passwordEncoder The encoder for hashing passwords.
	 */
	@Autowired
	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	/**
	 * Adds a new user to the repository after checking for existing username/email and encoding the password.
	 *
	 * @param user The user object to add. Must not be null.
	 * @return The saved user object with encoded password and generated ID.
	 * @throws RuntimeException if a user with the same username or email already exists.
	 */
	// add a user
	public User addUser(@NotNull(message = "User cannot be null") User user) {
		log.debug("Attempting to add new user with username: {}", user.getUsername());
		if (userRepository.findByUsername(user.getUsername()).isPresent()) {
			log.warn("Username '{}' already exists. Cannot add user.", user.getUsername());
			throw new RuntimeException("Username already exists");
		}
		if (userRepository.findByEmail(user.getEmail()).isPresent()) {
			log.warn("Email '{}' already exists. Cannot add user.", user.getEmail());
			throw new RuntimeException("Email already exists");
		}
		String rawPassword = user.getPassword();
		user.setPassword(passwordEncoder.encode(rawPassword));
		log.info("Encoded password for user: {}", user.getUsername());

		User savedUser = userRepository.save(user);
		log.info("Successfully added user with username: {} and ID: {}", savedUser.getUsername(), savedUser.getId());
		return savedUser;
	}

	/**
	 * Retrieves a user by their username.
	 *
	 * @param username The username to search for. Must not be null or blank.
	 * @return The found user object.
	 * @throws RuntimeException if no user is found with the given username.
	 */
	// get a user by username
	public User getUserByUsername(
			@NotNull(message = "Username cannot be null")
			@NotBlank(message = "Username cannot be blank")
			String username
	) {
		log.debug("Attempting to retrieve user by username: {}", username);
		return userRepository.findByUsername(username)
				.orElseThrow(() -> {
					log.warn("User not found with username: {}", username);
					return new RuntimeException("User not found with username: " + username);
				});
	}

	/**
	 * Retrieves a user by their email address.
	 *
	 * @param email The email address to search for. Must be a valid email format, not null or blank.
	 * @return The found user object.
	 * @throws RuntimeException if no user is found with the given email.
	 */
	// get a user by email
	public User getUserByEmail(
			@NotNull(message = "Email cannot be null")
			@NotBlank(message = "Email cannot be blank")
			@Email(message = "Email should be valid")
			String email
	) {
		log.debug("Attempting to retrieve user by email: {}", email);
		return userRepository.findByEmail(email)
				.orElseThrow(() -> {
					log.warn("User not found with email: {}", email);
					return new RuntimeException("User not found with email: " + email);
				});
	}

	/**
	 * Retrieves a user by their unique ID.
	 *
	 * @param id The ID of the user to retrieve. Must not be null.
	 * @return The found user object.
	 * @throws RuntimeException if no user is found with the given ID.
	 */
	// get a user by id
	public User getUserById(
			@NotNull(message = "Id cannot be null")
			Long id
	) {
		log.debug("Attempting to retrieve user by ID: {}", id);
		return userRepository.findById(id)
				.orElseThrow(() -> {
					log.warn("User not found with ID: {}", id);
					return new RuntimeException("User not found with ID: " + id);
				});
	}

	/**
	 * Updates an existing user. Finds the user by username before saving the provided user object.
	 * Note: This method saves the passed user object directly. Consider fetching the existing user
	 * by ID and updating specific fields for more robust update logic.
	 *
	 * @param user The user object containing updated information. Must not be null. The username is used to check existence.
	 * @return The saved user object.
	 * @throws RuntimeException if no user is found with the username specified in the input user object.
	 */
	// update a user
	public User updateUser(@NotNull(message = "User cannot be null") User user) {
		log.debug("Attempting to update user with username: {}", user.getUsername());
		// Check if user exists by username before attempting save
		Optional<User> existingUserOpt = userRepository.findByUsername(user.getUsername());
		if (existingUserOpt.isEmpty()) {
			log.warn("User not found with username: {}. Cannot update.", user.getUsername());
			throw new RuntimeException("User not found for update with username: " + user.getUsername());
		}
		// Consider fetching user by ID and updating fields instead of saving the passed object directly
		// User existingUser = getUserById(user.getId()); // Example: Fetch by ID
		// existingUser.setEmail(user.getEmail()); // Example: Update specific fields
		// existingUser.setRoles(user.getRoles());
		// if (user.getPassword() != null && !user.getPassword().isEmpty()) { // Example: Update password if provided
		//    existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
		// }
		User updatedUser = userRepository.save(user); // Saves the passed object
		log.info("Successfully updated user with username: {}", updatedUser.getUsername());
		return updatedUser;
	}

	/**
	 * Deletes a user. Finds the user by username before attempting deletion.
	 * Note: This method deletes the passed user object. Consider finding the user by ID
	 * and deleting that specific entity for safer deletion.
	 *
	 * @param user The user object to delete. Must not be null. The username is used to check existence.
	 * @throws RuntimeException if no user is found with the username specified in the input user object.
	 */
	// delete a user
	public void deleteUser(@NotNull(message = "User cannot be null") User user) {
		log.debug("Attempting to delete user with username: {}", user.getUsername());
		// Check if user exists by username before attempting delete
		Optional<User> userToDeleteOpt = userRepository.findByUsername(user.getUsername());
		if (userToDeleteOpt.isEmpty()) {
			log.warn("User not found with username: {}. Cannot delete.", user.getUsername());
			throw new RuntimeException("User not found for deletion with username: " + user.getUsername());
		}
		// It's generally safer to delete the entity found by ID or username
		// userRepository.delete(userToDeleteOpt.get()); // Example: Delete the found user
		userRepository.delete(user); // Deletes based on the passed object (requires ID to be correct)
		log.info("Successfully deleted user with username: {}", user.getUsername());
	}

	/**
	 * Retrieves all users from the repository.
	 *
	 * @return A list of all users.
	 * @throws RuntimeException if no users are found in the repository. (Note: Returning an empty list is often preferred).
	 */
	public List<User> getAllUsers() {
		log.debug("Attempting to retrieve all users.");
		List<User> users = userRepository.findAll();
		if (users.isEmpty()) {
			// Consider returning Collections.emptyList() instead of throwing an exception
			log.warn("No users found in the repository.");
			throw new RuntimeException("No users found");
		}
		log.info("Retrieved {} users.", users.size());
		return users;
	}
}
