package com.todo.rails.elite.starter.code.service.security;

import com.todo.rails.elite.starter.code.model.security.SecurityUser;
import com.todo.rails.elite.starter.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Autowired
	public JpaUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.map(SecurityUser::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
	}
}
