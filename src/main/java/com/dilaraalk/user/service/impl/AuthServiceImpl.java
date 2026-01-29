package com.dilaraalk.user.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dilaraalk.user.dto.DtoLoginRequest;
import com.dilaraalk.user.dto.DtoUserRegisterRequest;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import com.dilaraalk.user.service.IAuthService;
import com.dilaraalk.user.util.JwtUtil;
import com.dilaraalk.email.event.PasswordResetEvent;
import org.springframework.context.ApplicationEventPublisher;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void register(DtoUserRegisterRequest request) {

		if (userRepository.findByUserName(request.getUserName()).isPresent()) {
			throw new RuntimeException("Kullanıcı zaten var");
		}

		User user = new User();
		user.setUserName(request.getUserName());
		user.setEmail(request.getEmail());
		// parolayı BCrypt ile encode ediyoruz
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setRoles(List.of("ROLE_USER"));

		userRepository.save(user);

	}

	@Override
	public String login(DtoLoginRequest request) {

		Optional<User> optionalUser = userRepository.findByUserName(request.getUserName());
		if (!optionalUser.isPresent()) {
			throw new RuntimeException("Kullanıcı bulunamadı!");
		}

		User user = optionalUser.get();

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException("Şifre hatalı!");
		}

		return jwtUtil.generateToken(user.getUserName(), user.getRoles());
	}

	@Override
	public void forgotPassword(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı: " + email));

		// Demo token generation
		String token = java.util.UUID.randomUUID().toString();
		// TODO: Save token to DB/Redis with expiry

		// Frontend URL (hardcoded for now, should be from config)
		String resetLink = "http://localhost:5173/reset-password?token=" + token;

		eventPublisher.publishEvent(new PasswordResetEvent(
				this,
				user.getEmail(),
				user.getUserName(),
				resetLink));
	}

}