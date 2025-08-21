package com.dilaraalk.address.service;

import java.util.List;

import com.dilaraalk.address.dto.AddressRequestDto;
import com.dilaraalk.address.dto.AddressResponseDto;

public interface IAddressService {

    List<AddressResponseDto> getAddresses(Long userId);

    AddressResponseDto addAddress(Long userId, AddressRequestDto request);

    AddressResponseDto updateAddress(Long userId, Long addressId, AddressRequestDto request);

    void deleteAddress(Long userId, Long addressId);
	
	
}
