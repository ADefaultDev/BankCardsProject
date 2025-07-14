package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthResponseDTO;
import com.example.bankcards.dto.LoginRequestDTO;
import com.example.bankcards.dto.RegisterRequestDTO;
import com.example.bankcards.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void register_ShouldReturnAuthResponse() throws Exception {
        setup();

        // Используется JSON строка вместо объекта, чтобы обойти проблему с LocalDate
        String requestJson = """
        {
            "username": "user",
            "password": "password",
            "firstName": "FirstName",
            "secondName": "SecondName",
            "surname": "Surname",
            "birthday": "2000-12-12"
        }
        """;

        AuthResponseDTO response = new AuthResponseDTO("testToken", "testMessage");

        when(authService.register(any(RegisterRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("testToken"));
    }

    @Test
    void login_ShouldReturnAuthResponse() throws Exception {
        setup();
        LoginRequestDTO request = new LoginRequestDTO("user", "password");
        AuthResponseDTO response = new AuthResponseDTO("testToken", "testMessage");

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("testToken"));
    }

    @Test
    void register_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        setup();

        // Используется JSON строка с невалидными данными
        String invalidRequestJson = """
        {
            "username": "",
            "password": "",
            "firstName": "",
            "secondName": "",
            "surname": "",
            "birthday": "2000-12-12"
        }
        """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturnBadRequest_WhenInvalidData() throws Exception {
        setup();
        LoginRequestDTO request = new LoginRequestDTO("", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}