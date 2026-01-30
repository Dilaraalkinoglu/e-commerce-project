package com.dilaraalk.user.service.impl;

import com.dilaraalk.user.dto.DtoLoginRequest;
import com.dilaraalk.user.dto.DtoUserRegisterRequest;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import com.dilaraalk.user.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

        @Mock
        private UserRepository userRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtUtil jwtUtil;

        @Mock
        private com.dilaraalk.user.service.RefreshTokenService refreshTokenService;

        @Mock
        private org.springframework.context.ApplicationEventPublisher eventPublisher;

        @InjectMocks
        private AuthServiceImpl authService;

        @BeforeEach
        void setUp() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        void register_ShouldSaveNewUser_WhenUsernameNotExists() {
                // arrange
                DtoUserRegisterRequest request = new DtoUserRegisterRequest();
                request.setUserName("dilara");
                request.setPassword("12345");

                when(userRepository.findByUserName("dilara")).thenReturn(Optional.empty());
                when(passwordEncoder.encode("12345")).thenReturn("encodedPass");

                // act
                authService.register(request);

                // assert
                verify(userRepository).save(argThat(user -> user.getUserName().equals("dilara") &&
                                user.getPassword().equals("encodedPass") &&
                                user.getRoles().contains("ROLE_USER")));
        }

        @Test
        void register_ShouldThrowException_WhenUsernameAlreadyExists() {
                // arrange
                DtoUserRegisterRequest request = new DtoUserRegisterRequest();
                request.setUserName("dilara");
                request.setPassword("12345");

                when(userRepository.findByUserName("dilara"))
                                .thenReturn(Optional.of(new User()));

                // act & assert
                RuntimeException ex = assertThrows(RuntimeException.class,
                                () -> authService.register(request));

                assertEquals("Kullanıcı zaten var", ex.getMessage());
                verify(userRepository, never()).save(any());
        }

        @Test
        void login_ShouldReturnToken_WhenCredentialsValid() {
                // arrange
                DtoLoginRequest request = new DtoLoginRequest();
                request.setUserName("dilara");
                request.setPassword("12345");

                User user = new User();
                user.setUserName("dilara");
                user.setPassword("encodedPass");
                user.setRoles(List.of("ROLE_USER"));

                when(userRepository.findByUserName("dilara"))
                                .thenReturn(Optional.of(user));
                when(passwordEncoder.matches("12345", "encodedPass"))
                                .thenReturn(true);
                when(jwtUtil.generateToken(anyString(), anyList()))
                                .thenReturn("jwt-token");

                com.dilaraalk.user.entity.RefreshToken mockRefreshToken = new com.dilaraalk.user.entity.RefreshToken();
                mockRefreshToken.setToken("refresh-token");
                when(refreshTokenService.createRefreshToken(anyLong())).thenReturn(mockRefreshToken);

                // act
                com.dilaraalk.user.dto.JwtResponse response = authService.login(request);

                // assert
                assertEquals("jwt-token", response.getAccessToken());
                assertEquals("refresh-token", response.getRefreshToken());
        }

        @Test
        void login_ShouldThrowException_WhenUserNotFound() {
                // arrange
                DtoLoginRequest request = new DtoLoginRequest();
                request.setUserName("unknown");
                request.setPassword("12345");

                when(userRepository.findByUserName("unknown"))
                                .thenReturn(Optional.empty());

                // act & assert
                RuntimeException ex = assertThrows(RuntimeException.class,
                                () -> authService.login(request));

                assertEquals("Kullanıcı bulunamadı!", ex.getMessage());
        }

        @Test
        void login_ShouldThrowException_WhenPasswordIncorrect() {
                // arrange
                DtoLoginRequest request = new DtoLoginRequest();
                request.setUserName("dilara");
                request.setPassword("wrong");

                User user = new User();
                user.setUserName("dilara");
                user.setPassword("encodedPass");
                user.setRoles(List.of("ROLE_USER"));

                when(userRepository.findByUserName("dilara"))
                                .thenReturn(Optional.of(user));
                when(passwordEncoder.matches("wrong", "encodedPass"))
                                .thenReturn(false);

                // act & assert
                RuntimeException ex = assertThrows(RuntimeException.class,
                                () -> authService.login(request));

                assertEquals("Şifre hatalı!", ex.getMessage());
        }
}
