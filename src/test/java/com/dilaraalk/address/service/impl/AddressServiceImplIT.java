package com.dilaraalk.address.service.impl;

import com.dilaraalk.address.dto.AddressRequestDto;
import com.dilaraalk.address.dto.AddressResponseDto;
import com.dilaraalk.address.repository.AddressRepository;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import com.dilaraalk.common.test.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddressServiceImplIT extends BaseIntegrationTest {

    @Autowired
    private AddressServiceImpl addressService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    private User user;

    @BeforeEach
    void setUp() {
        addressRepository.deleteAll();
        userRepository.deleteAll();

        user = new User();
        user.setUserName("testuser");
        user.setEmail("testuser@test.com");
        user.setPassword("password");
        user = userRepository.save(user);
    }

    @Test
    void testAddAddress() {
        AddressRequestDto request = AddressRequestDto.builder()
                .title("Ev")
                .addressLine("Test Mah. 123")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34000")
                .country("Türkiye")
                .defaultAddress(true)
                .build();

        AddressResponseDto response = addressService.addAddress(user.getId(), request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Ev");
        assertThat(response.isDefaultAddress()).isTrue();
    }

    @Test
    void testGetAddresses() {
        AddressRequestDto request = AddressRequestDto.builder()
                .title("Ev")
                .addressLine("Test Mah. 123")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34000")
                .country("Türkiye")
                .defaultAddress(true)
                .build();
        addressService.addAddress(user.getId(), request);

        List<AddressResponseDto> addresses = addressService.getAddresses(user.getId());

        assertThat(addresses).hasSize(1);
        assertThat(addresses.get(0).getTitle()).isEqualTo("Ev");
    }

    @Test
    void testUpdateAddress() {
        AddressRequestDto request = AddressRequestDto.builder()
                .title("Ev")
                .addressLine("Test Mah. 123")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34000")
                .country("Türkiye")
                .defaultAddress(true)
                .build();
        AddressResponseDto added = addressService.addAddress(user.getId(), request);

        AddressRequestDto updateRequest = AddressRequestDto.builder()
                .title("İş")
                .addressLine("İş Mah. 456")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34001")
                .country("Türkiye")
                .defaultAddress(false)
                .build();

        AddressResponseDto updated = addressService.updateAddress(user.getId(), added.getId(), updateRequest);

        assertThat(updated.getTitle()).isEqualTo("İş");
        assertThat(updated.isDefaultAddress()).isFalse();
    }

    @Test
    void testDeleteAddress() {
        AddressRequestDto request = AddressRequestDto.builder()
                .title("Ev")
                .addressLine("Test Mah. 123")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34000")
                .country("Türkiye")
                .defaultAddress(true)
                .build();
        AddressResponseDto added = addressService.addAddress(user.getId(), request);

        addressService.deleteAddress(user.getId(), added.getId());

        List<AddressResponseDto> addresses = addressService.getAddresses(user.getId());
        assertThat(addresses).isEmpty();
    }

    @Test
    void testAddAddress_InvalidUser() {
        AddressRequestDto request = AddressRequestDto.builder()
                .title("Ev")
                .addressLine("Test Mah. 123")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34000")
                .country("Türkiye")
                .defaultAddress(true)
                .build();

        assertThrows(RuntimeException.class, () -> addressService.addAddress(999L, request));
    }
}
