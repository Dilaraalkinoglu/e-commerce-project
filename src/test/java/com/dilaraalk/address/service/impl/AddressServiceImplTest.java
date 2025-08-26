package com.dilaraalk.address.service.impl;

import com.dilaraalk.address.dto.AddressRequestDto;
import com.dilaraalk.address.dto.AddressResponseDto;
import com.dilaraalk.address.entity.Address;
import com.dilaraalk.address.repository.AddressRepository;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddressServiceImplTest {

    private AddressRepository addressRepository;
    private UserRepository userRepository;
    private AddressServiceImpl addressService;

    @BeforeEach
    void setUp() {
        addressRepository = mock(AddressRepository.class);
        userRepository = mock(UserRepository.class);
        addressService = new AddressServiceImpl(addressRepository, userRepository);
    }

    @Test
    void getAddresses_shouldReturnList() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Address address = new Address();
        address.setId(10L);
        address.setUser(user);
        when(addressRepository.findByUser(user)).thenReturn(List.of(address));

        List<AddressResponseDto> addresses = addressService.getAddresses(1L);

        assertEquals(1, addresses.size());
        assertEquals(10L, addresses.get(0).getId());
    }

    @Test
    void addAddress_shouldSaveAndReturnDto() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        AddressRequestDto request = new AddressRequestDto();
        request.setTitle("Home");
        request.setAddressLine("Street 1");
        request.setCity("City");
        request.setState("State");
        request.setPostalCode("12345");
        request.setCountry("Country");
        request.setDefaultAddress(true);

        Address savedAddress = new Address();
        savedAddress.setId(100L);
        savedAddress.setUser(user);
        savedAddress.setTitle(request.getTitle());
        when(addressRepository.save(any(Address.class))).thenReturn(savedAddress);

        AddressResponseDto response = addressService.addAddress(1L, request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        verify(addressRepository).save(any(Address.class));
    }

    @Test
    void updateAddress_shouldModifyAndReturnDto() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Address existing = new Address();
        existing.setId(50L);
        existing.setUser(user);
        when(addressRepository.findById(50L)).thenReturn(Optional.of(existing));

        AddressRequestDto request = new AddressRequestDto();
        request.setTitle("Work");
        request.setAddressLine("Street 2");
        request.setCity("City2");
        request.setState("State2");
        request.setPostalCode("54321");
        request.setCountry("Country2");
        request.setDefaultAddress(false);

        Address updated = new Address();
        updated.setId(50L);
        when(addressRepository.save(existing)).thenReturn(updated);

        AddressResponseDto response = addressService.updateAddress(1L, 50L, request);

        assertNotNull(response);
        assertEquals(50L, response.getId());
        verify(addressRepository).save(existing);
    }

    @Test
    void deleteAddress_shouldCallDelete() {
        User user = new User();
        user.setId(1L);
        Address address = new Address();
        address.setId(10L);
        address.setUser(user);

        when(addressRepository.findById(10L)).thenReturn(Optional.of(address));

        addressService.deleteAddress(1L, 10L);

        verify(addressRepository).delete(address);
    }
}
