package com.dilaraalk.address.controller;

import com.dilaraalk.address.dto.AddressRequestDto;
import com.dilaraalk.address.dto.AddressResponseDto;
import com.dilaraalk.address.repository.AddressRepository;
import com.dilaraalk.common.test.BaseIntegrationTest;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AddressControllerIT extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    private User user;

    @BeforeEach
    void setUp() {
        // Önce DB temizliği, foreign key hatasını önlemek için address önce
        addressRepository.deleteAll();
        userRepository.deleteAll();

        // Test user
        user = new User();
        user.setUserName("testuser");
        user.setEmail("testuser@test.com");
        user.setPassword("password");
        user = userRepository.save(user);
    }

    @Test
    void testAddAndGetAddress() throws Exception {
        AddressRequestDto request = AddressRequestDto.builder()
                .title("Ev")
                .addressLine("Test Mah. 123")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34000")
                .country("Türkiye")
                .defaultAddress(true)
                .build();

        mockMvc.perform(post("/api/user/me/address")
                .with(mockCustomUser(user, "USER"))  // <-- DB'deki user objesi
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(get("/api/user/me/address")
                .with(mockCustomUser(user, "USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<AddressResponseDto> addresses = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        assertThat(addresses).hasSize(1);
        assertThat(addresses.get(0).getTitle()).isEqualTo("Ev");
        assertThat(addresses.get(0).getId()).isNotNull();
    }

    @Test
    void testUpdateAddress() throws Exception {
        // Önce adres ekle
        AddressRequestDto addRequest = AddressRequestDto.builder()
                .title("Ev")
                .addressLine("Test Mah. 123")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34000")
                .country("Türkiye")
                .defaultAddress(true)
                .build();

        MvcResult addResult = mockMvc.perform(post("/api/user/me/address")
                .with(mockCustomUser(user, "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AddressResponseDto added = objectMapper.readValue(
                addResult.getResponse().getContentAsString(),
                AddressResponseDto.class
        );

        assertThat(added.getId()).isNotNull();

        // Update
        AddressRequestDto updateRequest = AddressRequestDto.builder()
                .title("İş")
                .addressLine("İş Mah. 456")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34001")
                .country("Türkiye")
                .defaultAddress(false)
                .build();

        MvcResult updateResult = mockMvc.perform(put("/api/user/me/address/" + added.getId())
                .with(mockCustomUser(user, "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AddressResponseDto updated = objectMapper.readValue(
                updateResult.getResponse().getContentAsString(),
                AddressResponseDto.class
        );

        assertThat(updated.getTitle()).isEqualTo("İş");
        assertThat(updated.getAddressLine()).isEqualTo("İş Mah. 456");
        assertThat(updated.isDefaultAddress()).isFalse();
    }

    @Test
    void testDeleteAddress() throws Exception {
        // Önce adres ekle
        AddressRequestDto addRequest = AddressRequestDto.builder()
                .title("Ev")
                .addressLine("Test Mah. 123")
                .city("İstanbul")
                .state("İstanbul")
                .postalCode("34000")
                .country("Türkiye")
                .defaultAddress(true)
                .build();

        MvcResult addResult = mockMvc.perform(post("/api/user/me/address")
                .with(mockCustomUser(user, "USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(addRequest)))
                .andExpect(status().isOk())
                .andReturn();

        AddressResponseDto added = objectMapper.readValue(
                addResult.getResponse().getContentAsString(),
                AddressResponseDto.class
        );

        assertThat(added.getId()).isNotNull();

        // Delete
        mockMvc.perform(delete("/api/user/me/address/" + added.getId())
                .with(mockCustomUser(user, "USER")))
                .andExpect(status().isNoContent());

        // Delete sonrası list kontrolü
        MvcResult result = mockMvc.perform(get("/api/user/me/address")
                .with(mockCustomUser(user, "USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<AddressResponseDto> addresses = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {}
        );

        assertThat(addresses).isEmpty();
    }
}
