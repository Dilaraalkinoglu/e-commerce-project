package com.dilaraalk.address.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddressRequestDto {
    
	@NotBlank
    private String title;
    
	@NotBlank
    private String addressLine;
    
	private String city;
    
	private String state;
    
	private String postalCode;
    
	private String country;
    
	private boolean defaultAddress;

}
