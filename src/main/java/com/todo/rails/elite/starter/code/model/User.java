package com.todo.rails.elite.starter.code.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", unique = true, nullable = false)
	@NotNull(message = "Username cannot be null")
	@NotBlank(message = "Username cannot be blank")
	private String username;

	@Column(name = "password", nullable = false)
	@NotNull(message = "Password cannot be null")
	@NotBlank(message = "Password cannot be blank")
	private String password;

	@Column(name = "email", unique = true, nullable = false)
	@NotNull(message = "Email cannot be null")
	@NotBlank(message = "Email cannot be blank")
	private String email;

	@Column(name = "roles", nullable = false)
	@NotNull(message = "Roles cannot be null")
	@NotBlank(message = "Roles cannot be blank")
	private String roles;

	public User() {
	}

	public User(String username, String password, String email, String roles) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", email='" + email + '\'' +
				", roles='" + roles + '\'' +
				'}';
	}
}
