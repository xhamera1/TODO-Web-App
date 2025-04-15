package com.todo.rails.elite.starter.code.controller.auth;

import com.todo.rails.elite.starter.code.exceptions.ResourceAlreadyExistsException;
import com.todo.rails.elite.starter.code.model.User;
import com.todo.rails.elite.starter.code.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles registration of new users.
 */
@Controller
@RequestMapping("/register")
public class AuthController {

	private final UserService userService;

	@Autowired
	public AuthController(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Displays the registration page.
	 *
	 * @param model View model to populate data for the frontend.
	 * @return Registration page view.
	 */
	@GetMapping
	public String showRegisterPage(Model model) {
		model.addAttribute("user", new User("", "", "", "USER"));
		return "register";
	}

	/**
	 * Handles user registration.
	 *
	 * @param user User data from the registration form.
	 * @param viewModel Model for redirect views.
	 * @return Redirect to login on success, or back to register on failure.
	 */
	@PostMapping
	public ModelAndView registerUser(@ModelAttribute("user") User user, Model viewModel) {
		try {
			userService.addUser(user);
			return new ModelAndView("redirect:/login");
		} catch (ResourceAlreadyExistsException ex) {
			return new ModelAndView("redirect:/register");
		}
	}
}
