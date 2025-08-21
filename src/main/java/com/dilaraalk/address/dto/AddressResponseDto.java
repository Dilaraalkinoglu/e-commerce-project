package com.dilaraalk.address.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AddressResponseDto {
    
	private Long id;
    
	private String title;
    
	private String addressLine;
    
	private String city;
    
	private String state;
    
	private String postalCode;
    
	private String country;
    
	private boolean defaultAddress;


}
