package com.dilaraalk.user.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.dilaraalk.user.dto.UserProfileResponseDto;
import com.dilaraalk.user.dto.UserProfileUpdateRequestDto;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import com.dilaraalk.user.service.IUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService{
	
	private final UserRepository userRepository;
	
	@Override
	public Optional<User> findByUserName(String userName) {
		return userRepository.findByUserName(userName);
	}

	@Override
	public UserProfileResponseDto getProfile(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
		
		UserProfileResponseDto dto = new UserProfileResponseDto();
		dto.setUserName(user.getUserName());
		dto.setEmail(user.getEmail());
		return dto;
	}

	@Override
	public UserProfileResponseDto updateProfile(Long userId, UserProfileUpdateRequestDto request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
		
		String oldEmail = user.getEmail();
		String newEmail = request.getEmail();
		
		//email degiştiyse not al 
	    if (!java.util.Objects.equals(oldEmail, newEmail)) {
	        System.out.println("NOT: Kullanıcı " + user.getUserName() 
	            + " email değiştiriyor, doğrulama yok!");
	    }
	    
		user.setUserName(request.getUserName());
		user.setEmail(request.getEmail());
		userRepository.save(user);
		
		//güncellenmis kullanıcıdan tekrar dto dönüyoruz
		UserProfileResponseDto dto = new UserProfileResponseDto();
		dto.setUserName(user.getUserName());
		dto.setEmail(user.getEmail());
				
		return dto;
	}
	
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }



}