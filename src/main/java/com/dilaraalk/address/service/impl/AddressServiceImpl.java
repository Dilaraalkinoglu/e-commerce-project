package com.dilaraalk.address.service.impl;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dilaraalk.address.dto.AddressRequestDto;
import com.dilaraalk.address.dto.AddressResponseDto;
import com.dilaraalk.address.entity.Address;
import com.dilaraalk.address.repository.AddressRepository;
import com.dilaraalk.address.service.IAddressService;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements IAddressService{
	private final AddressRepository addressRepository;
	private final UserRepository userRepository;
	
    @Override
    public List<AddressResponseDto> getAddresses(Long userId) {
        User user = getUser(userId);
        return addressRepository.findByUser(user)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AddressResponseDto addAddress(Long userId, AddressRequestDto request) {
        User user = getUser(userId);

        if (request.isDefaultAddress()) {
            unsetDefaultAddresses(user);
        }

        Address address = Address.builder()
                .user(user)
                .title(request.getTitle())
                .addressLine(request.getAddressLine())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .defaultAddress(request.isDefaultAddress())
                .build();

        Address saved = addressRepository.save(address);
        return mapToDto(saved);
    }

    @Override
    public AddressResponseDto updateAddress(Long userId, Long addressId, AddressRequestDto request) {
        User user = getUser(userId);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Adres bulunamadı"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu adrese erişim yetkiniz yok");
        }

        if (request.isDefaultAddress()) {
            unsetDefaultAddresses(user);
        }

        address.setTitle(request.getTitle());
        address.setAddressLine(request.getAddressLine());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setDefaultAddress(request.isDefaultAddress());

        Address updated = addressRepository.save(address);

        return mapToDto(updated);
    }

    @Override
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Adres bulunamadı"));

        if (!address.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bu adrese erişim yetkiniz yok");
        }

        addressRepository.delete(address);
    }
	
	
    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
    }

    private void unsetDefaultAddresses(User user) {
        addressRepository.findByUser(user).forEach(a -> {
            a.setDefaultAddress(false);
            addressRepository.save(a);
        });
    }

    private AddressResponseDto mapToDto(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .title(address.getTitle())
                .addressLine(address.getAddressLine())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .country(address.getCountry())
                .defaultAddress(address.isDefaultAddress())
                .build();
    }

}
