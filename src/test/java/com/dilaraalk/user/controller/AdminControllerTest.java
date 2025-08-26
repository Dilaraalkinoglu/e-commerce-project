package com.dilaraalk.user.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAdminDashboard_ShouldReturnOkMessage() {
        // act
        ResponseEntity<String> response = adminController.getAdminDashboard();

        // assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Sadece ADMIN rolü bu mesajı görebilir.", response.getBody());
    }
}
