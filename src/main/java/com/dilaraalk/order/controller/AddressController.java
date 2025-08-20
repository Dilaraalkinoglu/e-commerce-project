package com.dilaraalk.order.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dilaraalk.common.base.BaseController;
import com.dilaraalk.order.dto.AddressDto;
import com.dilaraalk.order.entity.Address;
import com.dilaraalk.order.repository.AddressRepository;
import com.dilaraalk.user.entity.User;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController extends BaseController{
	
	private final AddressRepository addressRepository; 
	
	
	@PostMapping
	public ResponseEntity<?> addAddress(@RequestBody AddressDto dto,@AuthenticationPrincipal User user){
		Address address = Address.builder()
				.street(dto.getStreet())
				.city(dto.getCity())
				.country(dto.getCountry())
				.zipCode(dto.getZipCode())
				.user(user)
				.build();
		
		addressRepository.save(address);
		
		return ResponseEntity.ok(dto);
	}
	
	@GetMapping
	public ResponseEntity<?> getMyAddresses(@AuthenticationPrincipal User user){
		List<Address> addresses = addressRepository.findByUser(user);
		return ResponseEntity.ok(addresses);
	}

}
