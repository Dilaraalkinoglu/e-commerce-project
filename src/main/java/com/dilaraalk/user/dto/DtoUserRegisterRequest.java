package com.dilaraalk.user.dto;

import com.dilaraalk.common.validation.ValidPassword;

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
	@ValidPassword
	private String password;

	@Email
	@NotBlank
	private String email;

}
