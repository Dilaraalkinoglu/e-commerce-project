package com.dilaraalk.user.service;

import com.dilaraalk.user.dto.DtoLoginRequest;
import com.dilaraalk.user.dto.DtoUserRegisterRequest;
import com.dilaraalk.user.dto.JwtResponse;
import com.dilaraalk.user.dto.TokenRefreshRequest;
import com.dilaraalk.user.dto.TokenRefreshResponse;

public interface IAuthService {

	void register(DtoUserRegisterRequest request);

	JwtResponse login(DtoLoginRequest request);

	TokenRefreshResponse refreshToken(TokenRefreshRequest request);

	void forgotPassword(String email);

}
