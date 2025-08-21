package com.dilaraalk.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DtoLoginRequest {
	
	@NotBlank
	private String userName;
	
	@NotBlank
	private String password;
	

}
