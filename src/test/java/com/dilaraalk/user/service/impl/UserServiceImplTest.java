package com.dilaraalk.user.service.impl;

import com.dilaraalk.user.dto.UserProfileResponseDto;
import com.dilaraalk.user.dto.UserProfileUpdateRequestDto;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;  // mock repo

    @InjectMocks
    private UserServiceImpl userService;    // gerçek servis (mock repo enjekte)

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUserName("dilara");
        user.setEmail("dilara@example.com");
    }

    @Test
    void testFindByUserName_UserExists() {
        when(userRepository.findByUserName("dilara")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByUserName("dilara");

        assertTrue(result.isPresent());
        assertEquals("dilara", result.get().getUserName());
        verify(userRepository, times(1)).findByUserName("dilara");
    }

    @Test
    void testFindByUserName_UserNotFound() {
        when(userRepository.findByUserName("unknown")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUserName("unknown");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUserName("unknown");
    }

    @Test
    void testGetProfile_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileResponseDto dto = userService.getProfile(1L);

        assertEquals("dilara", dto.getUserName());
        assertEquals("dilara@example.com", dto.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProfile_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getProfile(99L));

        assertEquals("Kullanıcı bulunamadı", ex.getMessage());
        verify(userRepository, times(1)).findById(99L);
    }

    @Test
    void testUpdateProfile_UserExists_EmailChanged() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserProfileUpdateRequestDto request = new UserProfileUpdateRequestDto();
        request.setUserName("newUser");
        request.setEmail("new@example.com");

        UserProfileResponseDto dto = userService.updateProfile(1L, request);

        assertEquals("newUser", dto.getUserName());
        assertEquals("new@example.com", dto.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserProfileUpdateRequestDto request = new UserProfileUpdateRequestDto();
        request.setUserName("doesntmatter");
        request.setEmail("doesntmatter@example.com");

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updateProfile(99L, request));

        assertEquals("Kullanıcı bulunamadı", ex.getMessage());
        verify(userRepository, times(1)).findById(99L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testFindById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("dilara", result.getUserName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.findById(99L));

        assertEquals("Kullanıcı bulunamadı", ex.getMessage());
        verify(userRepository, times(1)).findById(99L);
    }
}
