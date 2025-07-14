package com.example.bankcards.service;

import com.example.bankcards.dto.AuthResponseDTO;
import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.RegisterRequestDTO;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.exception.UsernameAlreadyExistsException;
import com.example.bankcards.repository.RoleRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_ShouldRegisterUser_WhenUsernameIsNew() {
        RegisterRequestDTO registerDto = new RegisterRequestDTO(
                "newUser",
                "password123",
                "John",
                "Doe",
                "Smith",
                LocalDate.of(1990,1,1)
        );

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        Role userRole = new Role();
        userRole.setRoleName("ROLE_USER");
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        AuthResponseDTO response = authService.register(registerDto);

        assertNotNull(response);
        assertEquals("Пользователь успешно зарегистрирован", response.message());

        verify(userRepository).existsByUsername("newUser");
        verify(passwordEncoder).encode("password123");
        verify(roleRepository).findByRoleName("ROLE_USER");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        RegisterRequestDTO registerDto = new RegisterRequestDTO(
                "existingUser",
                "password",
                "Jane",
                "Doe",
                "Smith",
                LocalDate.of(1990,1,1)
        );

        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class, () -> authService.register(registerDto));

        verify(userRepository).existsByUsername("existingUser");
        verifyNoMoreInteractions(userRepository, passwordEncoder, roleRepository);
    }

    @Test
    void register_ShouldThrowRuntimeException_WhenRoleNotFound() {
        RegisterRequestDTO registerDto = new RegisterRequestDTO(
                "newUser",
                "password",
                "John",
                "Doe",
                "Smith",
                LocalDate.of(1990,1,1)
        );

        when(userRepository.existsByUsername("newUser")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findByRoleName("ROLE_USER")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerDto));

        assertEquals("Роль USER не найдена", exception.getMessage());

        verify(userRepository).existsByUsername("newUser");
        verify(passwordEncoder).encode("password");
        verify(roleRepository).findByRoleName("ROLE_USER");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        LoginRequestDTO loginDto = new LoginRequestDTO("validUser", "password");

        User user = new User();
        user.setUsername("validUser");
        Role role = new Role();
        role.setRoleName("ROLE_USER");
        user.setRole(role);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.createToken(eq("validUser"), anyList()))
                .thenReturn("jwt-token");

        AuthResponseDTO response = authService.login(loginDto);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());
        assertEquals("Вход успешен", response.message());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("validUser");
        verify(jwtTokenProvider).createToken(eq("validUser"), anyList());
    }


    @Test
    void login_ShouldThrowUserNotFoundException_WhenUserNotExist() {
        LoginRequestDTO loginDto = new LoginRequestDTO("unknownUser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userRepository.findByUsername("unknownUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(loginDto));

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("unknownUser");
    }
}
