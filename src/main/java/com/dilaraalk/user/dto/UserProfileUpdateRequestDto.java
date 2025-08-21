package com.dilaraalk.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequestDto {
	
	@NotBlank(message = "Kullanıcı adı boş olamaz")
	private String userName;
	
	@Email(message = "Geçerli bir e-posta adresi girin")
	@NotBlank(message = "Email boş olamaz")
	private String email;

}
