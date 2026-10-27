package com.ikaansh.taskapi.service;

import com.ikaansh.taskapi.dto.AuthResponse;
import com.ikaansh.taskapi.dto.LoginRequest;
import com.ikaansh.taskapi.dto.RegisterRequest;
import com.ikaansh.taskapi.entity.User;
import com.ikaansh.taskapi.enums.Role;
import com.ikaansh.taskapi.repository.UserRepository;
import com.ikaansh.taskapi.security.CustomUserDetailsService;
import com.ikaansh.taskapi.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final AuthenticationManager authenticationManager;
	private final CustomUserDetailsService userDetailsService;

	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
		}

		Role assignedRole = (request.getRole() != null) ? request.getRole() : Role.ROLE_USER;

		User user = User.builder().name(request.getName()).email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword())).role(assignedRole).build();

		User savedUser = userRepository.save(user);
		UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
		String token = jwtUtil.generateToken(userDetails);

		return AuthResponse.builder().token(token).userId(savedUser.getId()).name(savedUser.getName())
				.email(savedUser.getEmail()).role(savedUser.getRole().name()).build();
	}

	public AuthResponse login(LoginRequest request) {
		authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

		User user = userRepository.findByEmail(request.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

		UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
		String token = jwtUtil.generateToken(userDetails);

		return AuthResponse.builder().token(token).userId(user.getId()).name(user.getName()).email(user.getEmail())
				.role(user.getRole().name()).build();
	}
}