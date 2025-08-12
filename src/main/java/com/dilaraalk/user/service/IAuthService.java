package com.dilaraalk.user.service;

import com.dilaraalk.user.dto.DtoLoginRequest;
import com.dilaraalk.user.dto.DtoUserRegisterRequest;

public interface IAuthService {

	void register(DtoUserRegisterRequest request);
	
	String login(DtoLoginRequest request);
	
}
