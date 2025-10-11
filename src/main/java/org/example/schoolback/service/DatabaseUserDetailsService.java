package org.example.schoolback.service;

import org.example.schoolback.entity.User;
import org.example.schoolback.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public DatabaseUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User account = userRepository.findByLogin(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

		GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + account.getRole());

		return org.springframework.security.core.userdetails.User.withUsername(account.getLogin())
			.password(account.getPassword())
			.authorities(authority)
			.accountExpired(false)
			.accountLocked(false)
			.credentialsExpired(false)
			.build();
	}
}


