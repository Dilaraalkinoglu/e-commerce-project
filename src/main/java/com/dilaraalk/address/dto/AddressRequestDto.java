package com.dilaraalk.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {
    
	@NotBlank
    private String title;
    
	@NotBlank
    private String addressLine;
    
	@NotBlank
	private String city;
    
	@NotBlank
	private String state;
    
	@NotBlank
	private String postalCode;
    
	@NotBlank
	private String country;
    
	private boolean defaultAddress;

}
