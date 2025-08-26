package com.dilaraalk.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DtoLoginRequest {
	
	@NotBlank
	private String userName;
	
	@NotBlank
	@Size(min = 5, message = "Şifre en az 5 karakter olmalı")
	private String password;
	

}
