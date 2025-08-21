package com.dilaraalk.user.service;

import java.util.Optional;

import com.dilaraalk.user.dto.UserProfileResponseDto;
import com.dilaraalk.user.dto.UserProfileUpdateRequestDto;
import com.dilaraalk.user.entity.User;

public interface IUserService {

	Optional<User> findByUserName(String userName);
	
	UserProfileResponseDto getProfile(Long userId);
	
	UserProfileResponseDto updateProfile(Long userId, UserProfileUpdateRequestDto request);

    User findById(Long id);

	
	
	
	
}
