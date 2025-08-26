package com.dilaraalk.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoUserRegisterRequest {

	@NotBlank
	@Size(min = 3, max = 20, message = "Kullanıcı adı 3-20 karakter arasında olmalı")
	private String userName;
	
	@NotBlank
	@Size(min = 5, message = "Şifre en az 5 karakter olmalı")
	private String password;
	
	@Email
	@NotBlank
	private String email;
	
}
